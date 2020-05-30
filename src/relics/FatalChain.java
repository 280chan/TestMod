package relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;

import mymod.TestMod;
import powers.FatalChainCheckDamagePower;

public class FatalChain extends MyRelic {
	public static final String ID = "FatalChain";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "每当你杀死敌人时，对所有敌人造成击杀溢出伤害的 #b2.718 倍的伤害。";//遗物效果的文本描叙。
	
	private static final HashMap<DamageInfo, AbstractCreature> MAP = new HashMap<DamageInfo, AbstractCreature>();
	public static final ArrayList<FatalChainCheckDamagePower> TO_REMOVE = new ArrayList<FatalChainCheckDamagePower>();
	
	public FatalChain() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}

	public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
		if (this.isActive && damageAmount > 0 && !target.isPlayer) {
			MAP.put(info, target);
			FatalChainCheckDamagePower p = new FatalChainCheckDamagePower(target, MAP);
			target.powers.add(p);
		}
	}
	
	public void onVictory() {
		if (this.isActive) {
			MAP.clear();
			TO_REMOVE.clear();
		}
	}
	
	public void update() {
		super.update();
		if (!this.isActive || TO_REMOVE.isEmpty())
			return;
		for (int i = 0; i < TO_REMOVE.size(); i++)
			TO_REMOVE.get(i).owner.powers.remove(TO_REMOVE.get(i));
		TO_REMOVE.clear();
	}

}