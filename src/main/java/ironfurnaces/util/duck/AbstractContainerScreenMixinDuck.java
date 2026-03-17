package ironfurnaces.util.duck;

public interface AbstractContainerScreenMixinDuck {
    default int modifyY(int slotY){
        return slotY;
    }
}
