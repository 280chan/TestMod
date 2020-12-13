package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import powers.JusticePower;

public class Justice extends AbstractTestRelic {
	public static final String ID = "Justice";
	
	public Justice() {
		super(ID, RelicTier.UNCOMMON, LandingSound.SOLID);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		this.addToTop(new ApplyPowerAction(p, p, new JusticePower(p, this)));
    }
	
	public void atTurnStart() {
		if (!isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		if (!JusticePower.hasThis(p))
			this.addToTop(new ApplyPowerAction(p, p, new JusticePower(p, this)));
    }
}