package com.community.site.Repository.FileRepository;

import com.community.site.entity.File;

import java.util.List;

public interface FileRepositoryCustom {
    List<File> findBySavedFileUrl(Long id);
}
