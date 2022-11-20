package com.community.site.Repository.FileRepository;

import com.community.site.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
    File findByFileUrl(String fileUrl);
}
