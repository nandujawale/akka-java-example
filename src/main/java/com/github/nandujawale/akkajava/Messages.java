package com.github.nandujawale.akkajava;

import java.util.Map;

public class Messages {

    public static class LogFile {

        private final String fileName;

        public LogFile(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class LogMessage {

        private final String data;

        public LogMessage(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    public static class LogMessageResult {

        private final String ipAddress;

        public LogMessageResult(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getIpAddress() {
            return ipAddress;
        }
    }

    public static class LogAnalysisResult {

        private final Map<String, Long> data;

        public LogAnalysisResult(Map<String, Long> data) {
            this.data = data;
        }

        public Map<String, Long> getData() {
            return data;
        }
    }
}
