package com.foober.foober.service;

import com.foober.foober.exception.ResourceNotFoundException;
import com.foober.foober.exception.UserNotFoundException;
import com.foober.foober.model.Image;
import com.foober.foober.model.User;
import com.foober.foober.repos.ImageRepository;
import com.foober.foober.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void save(String userEmail, MultipartFile file) throws IOException {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException();
        User user = optionalUser.get();
        Image image = new Image();
        image.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());
        image.setSize(file.getSize());
        image = imageRepository.save(image);
        user.setImage(image);
        userRepository.save(user);

    }

    public Image get(String id) {

        Optional<Image> image = imageRepository.findById(Long.parseLong(id));
        if (image.isEmpty())
            throw new ResourceNotFoundException("Image not found.");
        return image.get();

    }

}
