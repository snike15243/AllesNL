package com.allesnl.mini_app_service.service;

import com.allesnl.mini_app_service.contracts.miniapp.MiniAppInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MiniAppRegistryService {

    private final Map<Long, MiniAppInfo> miniApps = new ConcurrentHashMap<>();
    public MiniAppInfo register(MiniAppInfo miniAppInfo) {
        Long appId = (long) miniApps.size();
        miniAppInfo.setAppId(appId);
        miniApps.put(miniAppInfo.getAppId(), miniAppInfo);
        return miniAppInfo;
    }

    public void unregister(MiniAppInfo miniAppInfo) {
        miniApps.remove(miniAppInfo.getAppId());
    }

    public Optional<MiniAppInfo> get(Long id) {
        return Optional.ofNullable(miniApps.get(id));
    }

    public Collection<MiniAppInfo> getAll() {
        return Collections.unmodifiableCollection(miniApps.values());
    }

    public Optional<MiniAppInfo> replace(MiniAppInfo miniAppInfo) {
        return Optional.ofNullable(miniApps.replace(miniAppInfo.getAppId(), miniAppInfo));
    }
}
