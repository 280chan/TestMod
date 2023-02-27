package testmod.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.*;
import testmod.utils.MiscMethods;

public class LimitBreakPatch implements MiscMethods {
	private static int amount = 0;
	private static int[] amounts = new int[3];

	@SpirePatches({ @SpirePatch(clz = StrengthPower.class, method = "stackPower"),
			@SpirePatch(clz = StrengthPower.class, method = "reducePower"),
			@SpirePatch(clz = DexterityPower.class, method = "stackPower"),
			@SpirePatch(clz = DexterityPower.class, method = "reducePower"),
			@SpirePatch(clz = FocusPower.class, method = "stackPower"),
			@SpirePatch(clz = FocusPower.class, method = "reducePower") })
	public static class stackPrePatch {
		public static SpireReturn<Void> Prefix(AbstractPower _inst, int stackAmount) {
			amounts[_inst instanceof StrengthPower ? 0 : (_inst instanceof DexterityPower ? 1 : 2)] = _inst.amount;
			return SpireReturn.Continue();
		}
	}

	@SpirePatches({ @SpirePatch(clz = StrengthPower.class, method = "stackPower"),
			@SpirePatch(clz = DexterityPower.class, method = "stackPower"),
			@SpirePatch(clz = FocusPower.class, method = "stackPower") })
	public static class stackPostPatch {
		public static SpireReturn<Void> Postfix(AbstractPower _inst, int stackAmount) {
			int tmp = amounts[_inst instanceof StrengthPower ? 0 : (_inst instanceof DexterityPower ? 1 : 2)];
			if (tmp + stackAmount < 2147483646) {
				_inst.amount = tmp + stackAmount;
			} else {
				_inst.amount = 2147483646;
			}

			if (tmp + stackAmount > -2147483647) {
				_inst.amount = tmp + stackAmount;
			} else {
				_inst.amount = -2147483647;
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatches({ @SpirePatch(clz = StrengthPower.class, method = "reducePower"),
			@SpirePatch(clz = DexterityPower.class, method = "reducePower"),
			@SpirePatch(clz = FocusPower.class, method = "reducePower") })
	public static class reducePostPatch {
		public static SpireReturn<Void> Postfix(AbstractPower _inst, int stackAmount) {
			int tmp = amounts[_inst instanceof StrengthPower ? 0 : (_inst instanceof DexterityPower ? 1 : 2)];
			if (tmp - stackAmount < 2147483646) {
				_inst.amount = tmp - stackAmount;
			} else {
				_inst.amount = 2147483646;
			}

			if (tmp - stackAmount > -2147483647) {
				_inst.amount = tmp - stackAmount;
			} else {
				_inst.amount = -2147483647;
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = AbstractCreature.class, method = "addBlock")
	public static class blockPrePatch {
		public static SpireReturn<Void> Prefix(AbstractCreature _inst, int blockAmount) {
			if (_inst.isPlayer) {
				amount = _inst.currentBlock;
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = AbstractCreature.class, method = "addBlock")
	public static class blockPostPatch {
		public static SpireReturn<Void> Postfix(AbstractCreature _inst, int blockAmount) {
			if (_inst.isPlayer) {
				float tmp = blockAmount;
				tmp = MISC.replicaRelicStream().map(r -> MISC.get(r::onPlayerGainedBlock, b -> b.floatValue()))
						.reduce(MISC.t(), MISC::chain).apply(tmp);
				if (_inst.currentBlock + tmp > 999.0F) {
					if (_inst.currentBlock > 999) {
						tmp += (_inst.currentBlock - 999);
					}
					if (amount + tmp < 2.14748365E9F) {
						_inst.currentBlock = amount + MathUtils.floor(tmp);
					} else {
						_inst.currentBlock = 2147483640;
					}
				}
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(clz = AbstractPlayer.class, method = "increaseMaxOrbSlots")
	public static class MaxOrbsPatch {
		public static SpireReturn<Void> Prefix(AbstractPlayer _inst, int amount, boolean playSfx) {
			if (_inst.maxOrbs + amount < 2147483640) {
				if (playSfx) {
					CardCrawlGame.sound.play("ORB_SLOT_GAIN", 0.1F);
				}

				_inst.maxOrbs += amount;

				int i;
				for (i = 0; i < amount; i++) {
					_inst.orbs.add(new EmptyOrbSlot());
				}

				for (i = 0; i < _inst.orbs.size(); i++) {
					((AbstractOrb) _inst.orbs.get(i)).setSlot(i, _inst.maxOrbs);
				}
			}
			return SpireReturn.Return(null);
		}
	}

	@SpirePatch(clz = AbstractOrb.class, method = "setSlot")
	public static class setSlotPatch {
		public static SpireReturn<Void> Prefix(AbstractOrb _inst, int slotNum, int maxOrbs) {
			float dist = 160.0F * Settings.scale + slotNum * 3.0F * Settings.scale;
			float angle = 100.0F + maxOrbs * 12.0F;
			float offsetAngle = angle / 2.0F;
			angle *= slotNum / (maxOrbs - 1.0F);
			angle += 90.0F - offsetAngle;
			_inst.tX = dist * MathUtils.cosDeg(angle) + MISC.p().drawX;
			_inst.tY = dist * MathUtils.sinDeg(angle) + MISC.p().drawY + MISC.p().hb_h / 2.0F;
			if (maxOrbs == 1) {
				_inst.tX = MISC.p().drawX;
				_inst.tY = 160.0F * Settings.scale + MISC.p().drawY + MISC.p().hb_h / 2.0F;
			}

			_inst.hb.move(_inst.tX, _inst.tY);
			return SpireReturn.Return(null);
		}
	}
}
