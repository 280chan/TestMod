package deprecated.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.*;

import mymod.TestMod;
import relics.MyRelic;

/**
 * @deprecated
 */
public class BirthdayGift extends MyRelic {
	public static final String ID = "BirthdayGift";
	public static final String IMG = TestMod.cardIMGPath("relic1");
	public static final String DESCRIPTION = "拾起时，将金币清零，并获得所有未拥有的遗物非Boss加费遗物。";//遗物效果的文本描叙。
	private static final AbstractRelic[] RELICS = {new AncientTeaSet(), new ArtOfWar(), new HappyFlower(), new Lantern(), new GremlinHorn(), /*new Dodecahedron(),*/ new Sundial(), new Nunchaku() };
	
	public BirthdayGift() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public static void equipAction() {
		MyRelic.setTryEquip(BirthdayGift.class, false);
		AbstractPlayer p = AbstractDungeon.player;
		p.gold = 0;
		for (AbstractRelic r : RELICS) {
			TestMod.obtain(p, r, false);
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		this.setTryEquip(true);
    }
	
	public void atPreBattle() {
		if (this.isActive)
			AbstractDungeon.actionManager.addToBottom(
				new TalkAction(AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true), "生日快乐！", 0.5f, 0.5f));
    }
	
}