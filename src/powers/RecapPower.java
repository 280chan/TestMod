package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import utils.MiscMethods;

public class RecapPower extends AbstractTestPower implements MiscMethods {
	public static final String POWER_ID = "RecapPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	
	public RecapPower(AbstractCreature owner, int amount) {
		super(POWER_ID);
		this.name = NAME;
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	private void play(AbstractCard c) {
		this.addTmpActionToBot(() -> {
			this.playAgain(c,
					AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng));
		});
	}
	
    public void atStartOfTurn() {
    	this.list.forEach(this::play);
    	this.list.clear();
    }
    
    public void onAfterCardPlayed(final AbstractCard c) {
    	if (c.isInAutoplay)
    		return;
		this.list.add(c);
    	if (this.list.size() > this.amount)
    		this.list.remove(0);
    }
    
}
