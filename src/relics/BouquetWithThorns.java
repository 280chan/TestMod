package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;

public class BouquetWithThorns extends MyRelic{
	
	public static final String ID = "BouquetWithThorns";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每场战斗开始时，获得 #b1 层荆棘；每受到一次 #y攻击 ，增加 #b1 层荆棘；每受到一次 #y攻击 伤害，额外增加 #b1 层荆棘；如果受到类型为失去生命的伤害，增加失去生命点数的荆棘。";//遗物效果的文本描叙。
	
	public BouquetWithThorns() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}//文本更新方法，当你修改了DESCRIPTION时，调用该方法。
	
	private void increaseThorns(int amount) {
		if (!this.isActive)
			return;
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ThornsPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		increaseThorns(1);
		this.show();
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (!this.isActive)
			return damageAmount;
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			if (info.type == DamageInfo.DamageType.NORMAL) {
				this.show();
				if (damageAmount > 0) {
					increaseThorns(2);
				} else {
					increaseThorns(1);
				}
			} else if (info.type == DamageInfo.DamageType.HP_LOSS) {
				this.show();
				increaseThorns(damageAmount);
			}
		}
		return damageAmount;
    }
	
}