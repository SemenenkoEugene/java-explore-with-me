package ru.practicum.repository;

public interface ViewStatsProjection {
    String getApp();

    void setApp(String app);

    String getUri();

    void setUri(String uri);

    Long getHits();

    void setHits(Long hits);
}
