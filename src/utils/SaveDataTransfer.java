package utils;

public interface SaveDataTransfer {
    /**
     * From Struct Data to String
     * */
    public String Marshal();
    /**
     * Receive string to initialize
     * */
    public void UnMarshal(String data);
}
