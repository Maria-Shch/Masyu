package ru.shcherbatykh.spreader.model;

public record ComputationNodeKey(String remoteHost, String remotePort) {
    public String getLockKey() {
        return remoteHost + ":" + remotePort;
    }
}
