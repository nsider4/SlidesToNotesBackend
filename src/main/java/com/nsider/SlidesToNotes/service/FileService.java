package com.nsider.SlidesToNotes.service;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.service.processor.ImageProcessor;
import com.nsider.SlidesToNotes.service.processor.SlideProcessor;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Service to process PowerPoint files and extract their content.
 * It handles the concurrent processing of slides and manages notes using a counter service.
 */
@Service
@Slf4j
public class FileService {

    private final SlideProcessor slideProcessor;
    private final ImageProcessor imageProcessor;
    private final NoteCounterService noteCounterService;

    private ExecutorService executorService;
    private String lastFileChecksum = null;

    @Value("${fileService.threadPoolSize}")
    private int threadPoolSize;

    @Autowired
    public FileService(NoteCounterService noteCounterService) {
        this.slideProcessor = new SlideProcessor();
        this.imageProcessor = new ImageProcessor();
        this.noteCounterService = noteCounterService;
        initExecutorService();
    }

    /**
     * Initializes the executor service after the thread pool size is injected.
     */
    private void initExecutorService() {
        if (executorService == null) {
            if (threadPoolSize <= 0) {
                log.warn("Invalid threadPoolSize value: {}. Using default value of 4.", threadPoolSize);
                threadPoolSize = 4;
            }
            log.debug("Initializing executor service with threadPoolSize: {}", threadPoolSize);

            executorService = new ThreadPoolExecutor(
                    threadPoolSize,
                    threadPoolSize * 2, //Max threads
                    60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100), //Bounded queue with 100 slots
                    new ThreadPoolExecutor.CallerRunsPolicy() //Caller thread runs the task if the queue is full
            );
        }
    }

    /**
     * Processes a PPTX file and extracts its content, optionally excluding images.
     * 
     * @param file         the uploaded PPTX file to process, must not be {@code null}.
     * @param removeImages whether to exclude images from the extracted content.
     * @return the extracted content in HTML format.
     * @throws IOException if an I/O error occurs while processing the file.
     * @throws InterruptedException if the thread execution is interrupted.
     * @throws ExecutionException if an error occurs during the concurrent processing.
     */
    public String processPPTX(
            @Nonnull MultipartFile file, 
            boolean removeImages) throws IOException, InterruptedException, ExecutionException {
            
        try (InputStream inputStream = file.getInputStream()) {
            XMLSlideShow ppt = new XMLSlideShow(inputStream);
            double slideWidth = ppt.getPageSize().getWidth();
            double slideHeight = ppt.getPageSize().getHeight();

            List<Future<String>> futures = processSlides(ppt, slideWidth, slideHeight, removeImages);

            compareAndUpdateChecksum(file, lastFileChecksum);

            return collectSlideNotes(futures);
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error("Error processing PPTX file: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Computes the MD5 checksum of a given file.
     * 
     * @param file the file for which to compute the checksum; must not be {@code null}.
     * @return the MD5 checksum as a hexadecimal string; never {@code null}.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    private String computeFileChecksum(@Nonnull MultipartFile file) throws IOException {
        return DigestUtils.md5DigestAsHex(file.getInputStream());
    }

    /**
     * Compares the checksum of the current file with the last one, and increments the note counter
     * if they are different.
     * 
     * @param file the file whose checksum to check; must not be {@code null}.
     * @param lastFileChecksum the checksum of the previous file; must not be {@code null}.
     * @return the updated checksum of the current file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    private String compareAndUpdateChecksum(
            @Nonnull MultipartFile file, 
            @Nonnull String lastFileChecksum) throws IOException {

        String currentFileChecksum = computeFileChecksum(file);

        if (!currentFileChecksum.equals(lastFileChecksum)) {
            noteCounterService.incrementCounter();
        }

        return currentFileChecksum;
    }

    /**
     * Processes the slides concurrently.
     * 
     * @param ppt The PowerPoint presentation.
     * @param slideWidth The width of the slide.
     * @param slideHeight The height of the slide.
     * @param removeImages Whether to exclude images from the slide content.
     * @return A list of Future objects representing the processing of each slide.
     */
    private List<Future<String>> processSlides(
            XMLSlideShow ppt, double slideWidth, 
            double slideHeight, 
            boolean removeImages) {
            
        List<Future<String>> futures = new ArrayList<>();
        for (var slide : ppt.getSlides()) {
            StringWriter writer = new StringWriter();
            futures.add(executorService.submit(() -> {
                slideProcessor.processSlide(slide, slideWidth, slideHeight, removeImages, imageProcessor, writer);
                return writer.toString();
            }));
        }
        return futures;
    }

    /**
     * Collects the processed notes from the futures list.
     * 
     * @param futures The list of futures representing the processing of each slide.
     * @return The concatenated notes from all slides.
     * @throws InterruptedException if the thread execution is interrupted.
     * @throws ExecutionException if an error occurs during the concurrent processing.
     */
    private String collectSlideNotes(List<Future<String>> futures) throws InterruptedException, ExecutionException {
        StringBuilder notes = new StringBuilder();
        for (var future : futures) {
            try {
                notes.append(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error collecting slide notes: {}", e.getMessage());
                throw e;
            }
        }
        return notes.toString();
    }
}