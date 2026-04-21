package ironfurnaces.tileentity.furnaces.menu.handler;

import ironfurnaces.tileentity.furnaces.menu.Partition;
import ironfurnaces.tileentity.furnaces.menu.QuickMoveRuleBuilder;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;

import java.util.List;

public interface IMenuHandler {
    List<Partition> getPartitions();
    List<Partition> getInput();
    List<Partition> getOutput();
    List<DataSlot> getDataSlots();
    List<ContainerListener> getDataContainerListener();
    QuickMoveRuleBuilder buildRule(QuickMoveRuleBuilder builder);
}
