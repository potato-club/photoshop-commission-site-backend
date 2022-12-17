package com.community.site.Repository.OutputRepository;

import com.community.site.entity.Output;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutputRepository extends JpaRepository<Output, Long>, OutputRepositoryCustom {
    Output findByFileUrl(String fileUrl);
}
