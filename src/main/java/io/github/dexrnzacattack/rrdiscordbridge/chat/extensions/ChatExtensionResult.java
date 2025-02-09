package io.github.dexrnzacattack.rrdiscordbridge.chat.extensions;

public class ChatExtensionResult {
    public String string;
    public boolean sendMc;
    public boolean sendDiscord;

    ChatExtensionResult(String string, boolean sendMc, boolean sendDiscord) {
        this.string = string;
        this.sendMc = sendMc;
        this.sendDiscord = sendDiscord;
    }

    @Override
    public String toString() {
        return String.format("%s (mc: %b, dc: %b)", string, sendMc, sendDiscord);
    }
}
