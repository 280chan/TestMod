package potions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import mymod.TestMod;

public class EscapePotion extends AbstractPotion {
	public static final String POTION_ID = TestMod.makeID("EscapePotion");
	public static final String NAME = "丢人药水";
	public static final String[] DESCRIPTIONS = {"使一名非Boss敌人立即逃跑。"};

	public EscapePotion() {
		super(NAME, POTION_ID, PotionRarity.RARE, PotionSize.SPHERE, PotionColor.WHITE);
		this.potency = getPotency();
		this.description = this.getDesc();
		this.isThrown = true;
		this.targetRequired = true;
		this.tips.add(new PowerTip(this.name, this.description));
	}

	public String getDesc() {
		return DESCRIPTIONS[0];
	}

	public void use(AbstractCreature target) {
		AbstractMonster m = (AbstractMonster) target;
		boolean flipPlayer = false, darkling = false;
		if (m.type == EnemyType.BOSS)
			return;
		if (m.id.equals("Darkling")) {
			boolean alive = false;
			darkling = true;
			for (AbstractMonster c : AbstractDungeon.getMonsters().monsters) {
				if (!c.halfDead) {
					alive = true;
				}
			}
			if (alive) {
				m.halfDead = true;
				m.currentHealth = 0;
			} else {
				AbstractDungeon.getCurrRoom().cannotLose = false;
			}
		} else if (AbstractDungeon.player.hasPower("Surrounded")) {
			flipPlayer = true;
		}
		this.addToBot(new VFXAction(new SmokeBombEffect(m.hb.cX, m.hb.cY)));
		this.addToBot(new EscapeAction(m));
		if (darkling) {
			this.addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					this.isDone = true;
					AbstractDungeon.getMonsters().monsters.remove(m);
					for (AbstractMonster c : AbstractDungeon.getMonsters().monsters) {
						if (c.hasPower("Minion") && !c.isDying) {
							this.addToBot(new EscapeAction(c));
						}
					}
				}
			});
		} else if (flipPlayer) {
			this.addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					this.isDone = true;
					if (!m.hasPower("BackAttack")) {
						for (AbstractMonster c : AbstractDungeon.getCurrRoom().monsters.monsters) {
							if (!c.isDead && !c.isDying && !c.escaped && !c.isEscaping) {
								if (AbstractDungeon.player.hasPower("Surrounded")) {
									AbstractDungeon.player.flipHorizontal = (c.drawX < AbstractDungeon.player.drawX);
									System.out.println("flipHorizontal:" + AbstractDungeon.player.flipHorizontal);
								}
								if (c.hasPower("BackAttack")) {
									this.addToBot(new RemoveSpecificPowerAction(c, c, "BackAttack"));
								}
							}
						}
					} else {
						AbstractDungeon.player.flipHorizontal = false;
					}
					this.addToBot(new RemoveSpecificPowerAction(
							AbstractDungeon.player, AbstractDungeon.player, "Surrounded"));
				}
			});
		} else {
			this.addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					this.isDone = true;
					for (AbstractMonster c : AbstractDungeon.getMonsters().monsters) {
						if (c.hasPower("Minion") && !c.isDying) {
							this.addToBot(new EscapeAction(c));
						}
					}
				}
			});
		}
	}
	
	

	public AbstractPotion makeCopy() {
		return new EscapePotion();
	}

	public int getPotency(int ascensionLevel) {
		return 0;
	}
}
