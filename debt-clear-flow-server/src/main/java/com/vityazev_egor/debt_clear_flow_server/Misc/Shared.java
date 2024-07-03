package com.vityazev_egor.debt_clear_flow_server.Misc;

import java.nio.file.*;
// класс, который хранит статичные переменные для сервера
public class Shared {
    public static final Path csvFilesDirectory = Paths.get("csvFilesDirectory").toAbsolutePath();
}
