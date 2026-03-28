package ironfurnaces.gui.furnaces.component;

import ironfurnaces.tileentity.furnaces.menu.PagedGridPartition;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PageButton extends ImageButton {
    private final PagedGridPartition partition;
    private final Type type;

    public PageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation, OnPress onPress, PagedGridPartition partition, Type type) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.partition = partition;
        this.type = type;
    }

    public PageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, OnPress onPress, PagedGridPartition partition, Type type) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, onPress);
        this.partition = partition;
        this.type = type;
    }

    public PageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, PagedGridPartition partition, Type type) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress);
        this.partition = partition;
        this.type = type;
    }

    public PageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message, PagedGridPartition partition, Type type) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, message);
        this.partition = partition;
        this.type = type;
    }

    public enum Type {
        NEXT,
        PREVIOUS
    }



    @Override
    public void onPress() {
        switch (type){
            case NEXT -> partition.nextPage();
            case PREVIOUS -> partition.previousPage();
        }

    }


}
