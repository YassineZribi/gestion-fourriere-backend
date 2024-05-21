package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertSourceDto;
import com.yz.pferestapi.entity.Source;

public class SourceMapper {
    public static Source toEntity(UpsertSourceDto upsertSourceDto) {
        Source source = new Source();
        return map(upsertSourceDto, source);
    }

    public static Source toEntity(UpsertSourceDto upsertSourceDto, Source source) {
        return map(upsertSourceDto, source);
    }

    private static Source map(UpsertSourceDto upsertSourceDto, Source source) {
        source.setName(upsertSourceDto.getName());
        source.setDescription(upsertSourceDto.getDescription());
        return source;
    }
}
