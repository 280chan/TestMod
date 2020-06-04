package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class Antiphasic extends MyRelic {
	public static final String ID = "Antiphasic";
	
	public Antiphasic() {
		super(ID, RelicTier.UNCOMMON, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {//参数：info-伤害信息，damageAmount-伤害数值
        if (damage >= AbstractDungeon.player.maxHealth / 4.0) {
        	if (damage == AbstractDungeon.player.maxHealth && damage == 1) {
        		return damage;
        	}
        	AbstractDungeon.player.increaseMaxHp(5, true);
        }
		return damage;
    }

}