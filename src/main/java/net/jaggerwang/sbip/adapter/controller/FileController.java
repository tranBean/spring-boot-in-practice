package net.jaggerwang.sbip.adapter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.jaggerwang.sbip.adapter.controller.dto.RootDto;
import net.jaggerwang.sbip.entity.FileEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController extends AbstractController {
    @PostMapping("/upload")
    public RootDto upload(@RequestParam(required = false, defaultValue = "LOCAL") FileEntity.Region region,
            @RequestParam(required = false, defaultValue = "") String bucket,
            @RequestParam(required = false, defaultValue = "") String path,
            @RequestParam("file") List<MultipartFile> files) throws IOException {
        var fileEntities = new ArrayList<FileEntity>();
        for (var file : files) {
            var content = file.getBytes();

            var meta = FileEntity.Meta.builder().name(file.getOriginalFilename()).size(file.getSize())
                    .type(file.getContentType()).build();
            var fileEntity = FileEntity.builder().userId(loggedUserId()).region(region).bucket(bucket).meta(meta)
                    .build();

            fileEntity = fileUsecases.upload(path, content, fileEntity);

            fileEntities.add(fileEntity);
        }

        return new RootDto().addDataEntry("files",
                fileEntities.stream().map(fileEntity -> fullFileDto(fileEntity)).collect(Collectors.toList()));
    }

    @GetMapping("/info")
    public RootDto info(@RequestParam Long id) {
        var fileEntity = fileUsecases.info(id);

        return new RootDto().addDataEntry("file", fullFileDto(fileEntity));
    }
}
