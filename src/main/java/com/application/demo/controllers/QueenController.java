package com.application.demo.controllers;

import com.application.demo.entity.QueenFile;
import com.application.demo.entity.UploadFileResponse;
import com.application.demo.service.QueenStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class QueenController {
    private final Logger logger = LoggerFactory.getLogger(QueenController.class.getSimpleName());
    private final QueenStorageServiceImpl storageService;

    public QueenController(QueenStorageServiceImpl storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/queen")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Storing photo to db");
        UploadFileResponse response = null;
        try {
            response = storageService.store(file);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return response;
    }

    @GetMapping("/queen/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable long id) {
        logger.info("Getting photo from db");
        QueenFile queenFile = storageService.getFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + queenFile.getName() + "\"")
                .body(queenFile.getData());
    }


    @GetMapping("/queen/generate")
    public List<UploadFileResponse> generate() {

        return storageService.generate();

    }
}
