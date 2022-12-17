package com.community.site.Repository.OutputRepository;

import com.community.site.entity.Output;

import java.util.List;

public interface OutputRepositoryCustom {
    List<Output> findBySavedOutputUrl(Long id);
}
