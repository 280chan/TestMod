package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class BouquetWithThorns extends AbstractTestRelic {
	
	public BouquetWithThorns() {
		super(RelicTier.RARE, LandingSound.CLINK);
	}
	
	private void increaseThorns(int amount) {
		this.flash();
		this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ThornsPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atPreBattle() {
		increaseThorns(1);
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT && info.type != DamageType.THORNS) {
			increaseThorns(info.type == DamageType.NORMAL ? (damageAmount > 0 ? 2 : 1) : damageAmount);
		}
		return damageAmount;
    }
	
}