package testmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class LibrarianPower extends AbstractTestPower {
	private AbstractCard c;
	
	public LibrarianPower(AbstractCreature owner, AbstractCard c, int amount) {
		this.name += "[" + c.name + "]";
		this.ID += c.cardID;
		this.owner = owner;
		this.amount = amount;
		this.c = c;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		 this.description = desc(0) + this.amount + desc(1) + this.c.name + desc(2);
	}
	
    public void atStartOfTurn() {
    	this.amount--;
    	this.flashWithoutSound();
    	if (this.amount == 0)
    		this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    	else
        	this.updateDescription();
    }
    
    public void onVictory() {
    	this.flash();
    	AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.c.makeCopy(),
				Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
    }
    
}
