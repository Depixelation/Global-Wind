package colossalrenders.globalwind;

public interface PlayerInterface {
    public boolean wasOutside();
    public void setOutside(Boolean outside);
    public boolean isOutside(int threshold);
}
