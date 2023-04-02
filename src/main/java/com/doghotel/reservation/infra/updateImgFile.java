package com.doghotel.reservation.infra;

import com.doghotel.reservation.global.aws.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public abstract class updateImgFile <Entity, DTO>{

    public void updateFiles(Long id, List<MultipartFile> files) {
        List<Entity> entities = findById(id);
        List<DTO> dtos = convertImageToDTOs(files);
        deleteList(entities, dtos);
        addList(entities, dtos);
    }
    public abstract List<Entity> findById(Long id);

    public abstract List<DTO> convertImageToDTOs(List<MultipartFile> files);

    public abstract void deleteList(List<Entity> entities, List<DTO> dtos);

    public abstract void addList(List<Entity> entities, List<DTO> dtos);
}
