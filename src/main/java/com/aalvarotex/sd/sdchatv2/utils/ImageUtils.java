/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Base64;
import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author alvar
 */
@Named
@ManagedBean
@SessionScoped
public class ImageUtils implements Serializable{

    /**
     *
     * @param file
     * @return base64 de la imagen
     */
    public String upload(UploadedFile file) {
        String base64Image = "";
        if (file != null) {
            try (InputStream inputStream = file.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] fileBytes = outputStream.toByteArray();
                base64Image = Base64.getEncoder().encodeToString(fileBytes);
                // System.out.println(base64Image);
                // Optionally, you can save base64Image to a database or use it as needed.

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return base64Image;
    }
}
