package com.mohit.faceverification.service;

import com.mohit.faceverification.dto.PythonVerifyResponse;
import com.mohit.faceverification.dto.RegisterResponse;
import com.mohit.faceverification.dto.VerifyResponse;
import com.mohit.faceverification.entity.User;
import com.mohit.faceverification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.nio.file.*;

import java.io.IOException;
import java.nio.file.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public UserService(UserRepository userRepository,
                       RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public RegisterResponse registerUser(String name,
                                         MultipartFile image)
            throws IOException {

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + image.getOriginalFilename();

        Path targetPath = uploadPath.resolve(fileName);

        Files.copy(
                image.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        User user = new User();
        user.setName(name);
        user.setImagePath(targetPath.toString());

        User savedUser =
                userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getImagePath()
        );
    }
    public VerifyResponse verifyUser(
            Long userId,
            MultipartFile image) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        FileSystemResource registeredImage =
                new FileSystemResource(user.getImagePath());

        Path tempFile = Paths.get(uploadDir)
                .resolve("verify_" + System.currentTimeMillis() + ".jpg");

        Files.copy(
                image.getInputStream(),
                tempFile,
                StandardCopyOption.REPLACE_EXISTING);

        FileSystemResource verifyImage =
                new FileSystemResource(tempFile.toFile());

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("image1", registeredImage);
        body.add("image2", verifyImage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<PythonVerifyResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:8000/compare",
                        requestEntity,
                        PythonVerifyResponse.class);

        System.out.println("Status = " + response.getStatusCode());

        PythonVerifyResponse result = response.getBody();

        if (result == null) {
            throw new RuntimeException(
                    "No response received from Python service");
        }

        System.out.println("Verified = " + result.isVerified());
        System.out.println("Score = " + result.getScore());

        return new VerifyResponse(
                userId,
                result.isVerified(),
                result.getScore()
        );
    }
}