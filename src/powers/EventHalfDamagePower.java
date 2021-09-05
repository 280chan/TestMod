package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import relics.AscensionHeart;

public class EventHalfDamagePower extends AbstractTestPower implements InvisiblePower {
	public static final String POWER_ID = "EventHalfDamagePower";
	private static AscensionHeart ah = null;
	
	public static boolean hasThis() {
		return AbstractDungeon.player.powers.stream().anyMatch(p -> {return p instanceof EventHalfDamagePower;});
	}
	
	public static AbstractPower getThis() {
		return AbstractDungeon.player.powers.stream().filter(p -> {return p instanceof EventHalfDamagePower;})
				.findAny().orElse(null);
	}
	
	public EventHalfDamagePower(AbstractCreature owner, AscensionHeart relic) {
		super(POWER_ID);
		this.name = POWER_ID;
		this.owner = owner;
		this.amount = -1;
		ah = relic;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = "";
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
	}
	
    public int onLoseHp(int damage) {
    	if (AbstractDungeon.currMapNode != null)
        	if (AbstractDungeon.getCurrRoom() instanceof EventRoom)
        		if (AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
            		if (ah.checkLevel(15))
            			damage /= 2;
        return damage;
    }

}
