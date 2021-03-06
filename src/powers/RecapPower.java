package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
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

	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	
	public void clear() {
		this.list.clear();
	}
	
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
	
    public void atStartOfTurn() {
		for (AbstractCard c : this.list) {
			this.addToBot(new AbstractGameAction() {
				public void update() {
					RecapPower.this.playAgain(c, AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true,
							AbstractDungeon.cardRandomRng));
					this.isDone = true;
				}
			});
		}
    	this.clear();
    }
    
    public void onAfterCardPlayed(final AbstractCard c) {
    	if (c.isInAutoplay)
    		return;
		this.list.add(c);
    	if (this.list.size() > this.amount)
    		this.list.remove(0);
    }
    
}
