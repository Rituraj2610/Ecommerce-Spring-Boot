package com.rituraj.ecommerce.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rituraj.ecommerce.exception.EntityDeletionException;
import com.rituraj.ecommerce.exception.EntityPushException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        System.out.println("Cloudinary: " + cloudinary.getUserAgent());

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        if(!uploadResult.isEmpty()){
            return uploadResult.get("url").toString(); // Extract and return the URL
        }
        throw new EntityPushException("Failed to upload images to db");

    }

    public void deleteImageFromCloudinary(String publicId) {
        try {

            // Deleting the image from Cloudinary using its public ID
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new EntityDeletionException("Failed to delete image from db");
        }
    }

    public String extractPublicIdFromUrl(String url) {
        // Assuming the URL is in Cloudinary's default format
        String[] urlParts = url.split("/v");
        return urlParts[1].split("/")[1].replace(".jpg", ""); // Or adapt for your file extension
    }

}

