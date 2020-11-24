package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class BouquetWithThorns extends AbstractTestRelic{
	public static final String ID = "BouquetWithThorns";
	
	public BouquetWithThorns() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void increaseThorns(int amount) {
		this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ThornsPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atPreBattle() {
		increaseThorns(1);
		this.flash();
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			if (info.type == DamageType.NORMAL) {
				this.flash();
				if (damageAmount > 0) {
					increaseThorns(2);
				} else {
					increaseThorns(1);
				}
			} else if (info.type == DamageType.HP_LOSS) {
				this.flash();
				increaseThorns(damageAmount);
			}
		}
		return damageAmount;
    }
	
}