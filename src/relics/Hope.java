package relics;

import java.lang.reflect.InvocationTargetException;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.monsters.beyond.Darkling;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile.SaveType;
import com.megacrit.cardcrawl.vfx.GameSavedEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import actions.HopeAction;
import mymod.TestMod;

public class Hope extends AbstractTestRelic {

	public static Random HPRng = new Random();
	public static Random cardRng = new Random();
	public static final int RATE = 618;
	public static final int RANGEHP = 100000;
	public static final int RANGECARD = 10000;
	public static final int DELTA = 618;
	
	private boolean canDraw;
	private boolean disabledUntilEndOfTurn;
	
	public Hope() {
		super(RelicTier.UNCOMMON, LandingSound.MAGICAL);
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[2];
		if (this.counter == 0)
			return DESCRIPTIONS[2];
		float rateHP = (RATE + this.counter * DELTA) / 1000f;
		return DESCRIPTIONS[0] + rateHP + DESCRIPTIONS[1];
	}
	
	public int onAttacked(final DamageInfo info, final int damage) {
		AbstractRoom r = AbstractDungeon.getCurrRoom();
		if (info.type == DamageType.NORMAL && damage > 0 && r.phase == RoomPhase.COMBAT && roll(HPRng)) {
        	show();
			AbstractPlayer p = AbstractDungeon.player;
        	p.currentHealth = p.maxHealth;
        	p.healthBarUpdatedEvent();
        	boolean fuckedUndead = false;
        	for (AbstractMonster m : r.monsters.monsters) {
        		if (m.isDeadOrEscaped())
        			continue;
        		m.currentHealth = 0;
        		m.healthBarUpdatedEvent();
        		if (m instanceof AwakenedOne || m instanceof Darkling) {
        			r.cannotLose = false;
        		}
        		if (!m.id.equals("paleoftheancients:Reimu")) {
        			TestMod.info("希望:尝试秒杀" + m.name);
        			m.die();
        		} else {
        			TestMod.info("希望:尝试跳过秒杀先古境地灵梦，尝试添加其对应遗物...");
        			Class<? extends AbstractDungeon> c = CardCrawlGame.dungeon.getClass();
					try {
						c.getMethod("addRelicReward", String.class).invoke(null,
								"paleoftheancients:SoulOfTheShrineMaiden");
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
        		}
        		if (!m.isDying && !m.hasPower("Minion")) {
        			r.cannotLose = false;
        			fuckedUndead = true;
        		}
        		if (m.currentBlock > 0) {
					m.loseBlock();
				}
        	}
        	if (fuckedUndead) {
				this.addToBot(new AbstractGameAction() {
					@Override
					public void update() {
						this.isDone = true;
						if (!(r instanceof EventRoom)) {
							AbstractDungeon.actionManager.clearPostCombatActions();
							r.isBattleOver = true;
							r.phase = RoomPhase.COMPLETE;
							if ((!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss))
									|| (!(CardCrawlGame.dungeon instanceof TheBeyond)) || (Settings.isEndless)) {
								CardCrawlGame.sound.play("VICTORY");
							}
							if (((r instanceof MonsterRoomBoss)) && (!AbstractDungeon.loading_post_combat)) {
								if (!CardCrawlGame.loadingSave) {
									if (Settings.isDailyRun) {
										r.addGoldToRewards(100);
									} else {
										int tmp = 100 + AbstractDungeon.miscRng.random(-5, 5);
										if (AbstractDungeon.ascensionLevel >= 13) {
											r.addGoldToRewards(MathUtils.round(tmp * 0.75F));
										} else {
											r.addGoldToRewards(tmp);
										}
									}
								}
								if (ModHelper.isModEnabled("Cursed Run")) {
									AbstractDungeon.effectList
											.add(new ShowCardAndObtainEffect(AbstractDungeon.returnRandomCurse(),
													Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
								}
							} else if (((r instanceof MonsterRoomElite)) && (!AbstractDungeon.loading_post_combat)) {
								if ((CardCrawlGame.dungeon instanceof Exordium)) {
									CardCrawlGame.elites1Slain += 1;
								} else if ((CardCrawlGame.dungeon instanceof TheCity)) {
									CardCrawlGame.elites2Slain += 1;
								} else if ((CardCrawlGame.dungeon instanceof TheBeyond)) {
									CardCrawlGame.elites3Slain += 1;
								} else {
									CardCrawlGame.elitesModdedSlain += 1;
								}
								if (!CardCrawlGame.loadingSave) {
									if (Settings.isDailyRun) {
										r.addGoldToRewards(30);
									} else {
										r.addGoldToRewards(AbstractDungeon.treasureRng.random(25, 35));
									}
								}
							} else if (((r instanceof MonsterRoom))
									&& (!AbstractDungeon.getMonsters().haveMonstersEscaped())) {
								CardCrawlGame.monstersSlain += 1;
								if (Settings.isDailyRun) {
									r.addGoldToRewards(15);
								} else {
									r.addGoldToRewards(AbstractDungeon.treasureRng.random(10, 20));
								}
							}
							if ((!(r instanceof MonsterRoomBoss))
									|| ((!(CardCrawlGame.dungeon instanceof TheBeyond))
											&& (!(CardCrawlGame.dungeon instanceof TheEnding)))
									|| (Settings.isEndless)) {
								if (!AbstractDungeon.loading_post_combat) {
									r.dropReward();
									r.addPotionToRewards();
								}
								int card_seed_before_roll = AbstractDungeon.cardRng.counter;
								int card_randomizer_before_roll = AbstractDungeon.cardBlizzRandomizer;
								if (r.rewardAllowed) {
									if (r.mugged) {
										AbstractDungeon.combatRewardScreen.openCombat(AbstractRoom.TEXT[0]);
									} else if (r.smoked) {
										AbstractDungeon.combatRewardScreen.openCombat(AbstractRoom.TEXT[1], true);
									} else {
										AbstractDungeon.combatRewardScreen.open();
									}
									if ((!CardCrawlGame.loadingSave) && (!AbstractDungeon.loading_post_combat)) {
										SaveFile saveFile = new SaveFile(SaveType.POST_COMBAT);
										saveFile.card_seed_count = card_seed_before_roll;
										saveFile.card_random_seed_randomizer = card_randomizer_before_roll;
										if (r.combatEvent) {
											saveFile.event_seed_count -= 1;
										}
										SaveAndContinue.save(saveFile);
										AbstractDungeon.effectList.add(new GameSavedEffect());
									} else {
										CardCrawlGame.loadingSave = false;
									}
									AbstractDungeon.loading_post_combat = false;
								}
							}
						}
					}
				});
        	}
        	return 0;
        }
		return damage;
    }
	
	private boolean roll(Random rng) {
		boolean result = false;
		boolean hp = rng.equals(HPRng);
		if (hp) {
			if (this.isLocalTesting())
				return true;
			result = rng.random(RANGEHP) < RATE + this.counter * DELTA;
			if (!result) {
				this.counter++;
			} else {
				this.counter = 0;
			}
		} else {
			result = rng.random(RANGECARD) < RATE;
		}
		this.updateDescription(AbstractDungeon.player.chosenClass);
		return result;
	}

	public void disableUntilTurnEnds() {
		this.disabledUntilEndOfTurn = true;
	}
	
	public void atPreBattle() {
		HPRng = this.copyRNG(AbstractDungeon.miscRng);
		cardRng = this.copyRNG(AbstractDungeon.miscRng);
		this.canDraw = false;
	}

	public void atTurnStart() {
		this.canDraw = true;
		this.disabledUntilEndOfTurn = false;
	}

	public void onRefreshHand() {
		if ((AbstractDungeon.actionManager.actions.isEmpty()) && (AbstractDungeon.player.hand.isEmpty())
				&& (!AbstractDungeon.actionManager.turnHasEnded) && (this.canDraw) && (!AbstractDungeon.isScreenUp)) {
			if ((AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) && (!this.disabledUntilEndOfTurn)) {
				TestMod.info("希望随机中:...");
				if (roll(cardRng)) {
					TestMod.info("成功");
					show();
					this.addToTop(new HopeAction());
				} else {
					TestMod.info("失败");
				}
			}
		}
	}
	
}