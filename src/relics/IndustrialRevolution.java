package relics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import mymod.TestMod;
import powers.IndustrialRevolutionPower;
import powers.InorganicPower;

public class IndustrialRevolution extends MyRelic{
	public static final String ID = "IndustrialRevolution";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "拥有过 #y人工制品 的敌人，无法给其自身增加增益状态，无法给玩家增加负面状态。";

	public static final ArrayList<AbstractMonster> LIST = new ArrayList<AbstractMonster>();
	
	public IndustrialRevolution() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.COMMON, LandingSound.FLAT);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		LIST.clear();
		AbstractPlayer p = AbstractDungeon.player;
		p.powers.add(new IndustrialRevolutionPower(p));
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && m.hasPower("Artifact")) {
				m.powers.add(new InorganicPower(m));
				LIST.add(m);
			}
		}
    }
	

	private static void tryAdd() {
		AbstractPlayer p = AbstractDungeon.player;
		if (!p.hasPower(IndustrialRevolutionPower.POWER_ID)) {
			p.powers.add(new IndustrialRevolutionPower(p));
		}
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDead && !m.isDying && !m.halfDead && !m.hasPower(InorganicPower.POWER_ID)) {
				if (m.hasPower("Artifact")) {
					m.powers.add(new InorganicPower(m));
					LIST.add(m);
				} else if (LIST.contains(m)) {
					m.powers.add(new InorganicPower(m));
				}
			}
		}
	}
	
	public void atTurnStart() {
		if (!isActive)
			return;
		tryAdd();
    }//触发时机：在玩家回合开始时。
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		tryAdd();
    }//触发时机：在玩家回合结束时。
	
}