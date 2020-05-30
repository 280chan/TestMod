package powers;

import java.util.ArrayList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import mymod.TestMod;
import utils.MiscMethods;

public class RecapPower extends AbstractPower implements MiscMethods {
	public static final String POWER_ID = "RecapPower";
	public static final String NAME = "前情提要";
    public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = {"每回合开始，打出你上一回合打出的最后 #b"," 张牌一次。(限玩家打出)"};

	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	
	public void clear() {
		this.list.clear();
	}
	
	public RecapPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
	}
	
    public void atStartOfTurn() {
		for (AbstractCard c : this.list) {
			AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
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
