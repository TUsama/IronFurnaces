package ironfurnaces.gui.furnaces.component;

public class PositionContext {
    public int buttonX;
    public int y;
    public int panelX;
    public int baseX;
    public int baseY;
    public int basePanelX;

    public PositionContext() {
    }

    public void setBase(int x, int y, int panelX){
        this.baseX = x;
        this.baseY = y;
        this.basePanelX = panelX;
        this.zero();
    }

    public void moveDown(int y){
        this.y += y;
    }

    public void zero(){
        this.buttonX = baseX;
        this.y = baseY;
        this.panelX = basePanelX;
    }

}
