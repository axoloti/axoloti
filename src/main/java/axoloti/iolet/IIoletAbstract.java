package axoloti.iolet;

public interface IIoletAbstract {
    public void PostConstructor();
    public void disconnect();
    
    @Deprecated
    public void deleteNet();
}