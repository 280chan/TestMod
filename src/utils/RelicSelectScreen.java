package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;

import basemod.BaseMod;
import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 彼君不触
 * @version 11/30/2020
 * @since 8/29/2018
 */
public abstract class RelicSelectScreen implements RenderSubscriber, PreUpdateSubscriber, ScrollBarListener {
	private static final String MOD_ID = "Your Mod ID";
	
	private static final float X = 1670.0F * Settings.scale;
	private static final float Y = 500.0F * Settings.scale;
	private static final float TEXT_X = 1400.0F * Settings.scale;
	private static final float SPACE_Y = 58.0F * Settings.scale;
	
	private static final float SPACE = 80.0F * Settings.scale;
	private static final float START_X = 600.0F * Settings.scale;
	private static final float START_Y = Settings.HEIGHT - 300.0F * Settings.scale;
	private float scrollY = START_Y;
	private float targetY = this.scrollY;
	private float scrollLowerBound = 1000.0F * Settings.scale;
	private float scrollUpperBound = 3500.0F * Settings.scale;
	private boolean grabbedScreen = false;
	private float grabStartY = 0.0F;
	private ScrollBar scrollBar;
	private int row = 0;
	private ConfirmButton button;
	private static final Color RED_OUTLINE_COLOR = Settings.RED_RELIC_COLOR.cpy();
	private static final Color GREEN_OUTLINE_COLOR = Settings.GREEN_RELIC_COLOR.cpy();
	private static final Color BLUE_OUTLINE_COLOR = Settings.BLUE_RELIC_COLOR.cpy();
	private static final Color PURPLE_RELIC_COLOR = Settings.PURPLE_RELIC_COLOR.cpy();
	private static final Color BLACK_OUTLINE_COLOR = Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR.cpy();
	private AbstractRelic hoveredRelic;
	private AbstractRelic clickStartedRelic;
	private String bottomDesc = "This is a bottom prompt example";
	private String infoTitle = "This is an info title example";
	private String infoDesc = "This is an info description example";
	private static boolean isPrevMouseDown = false;
	private static RelicSelectScreen subscriber = null;
	private boolean autoSort = false;
	private int amountToSelect = 1;
	private boolean anyNum = false;
	private ArrayList<String> category = new ArrayList<String>();
	private ArrayList<ArrayList<AbstractRelic>> sortedRelics = new ArrayList<ArrayList<AbstractRelic>>();
	
	protected boolean rejectSelection = false;
	
	/**
	 * 被选中的遗物 The relics selected
	 */
	protected ArrayList<AbstractRelic> selectedRelics = new ArrayList<AbstractRelic>();
	
	/**
	 * 被选中的遗物 The relic selected
	 */
	protected AbstractRelic selectedRelic;
	
	/**
	 * 用于选择遗物的遗物列表 The relics which you select from
	 */
	protected ArrayList<AbstractRelic> relics = new ArrayList<AbstractRelic>();
	
	/**
	 * 该窗口当前是否开启，开启之前不会显示内容 Whether the screen is open
	 */
	protected boolean isOpen = false;
	
	/**
	 * 当前的选择窗口，设计上不支持同时存在多个遗物选择窗口，所以使用这个静态变量来存储当前窗口
	 * Current relic select screen, since it makes no sense to have two relic select on at same time
	 */
	protected static RelicSelectScreen screen = null;
	
	/**
	 * 在你的Mod主类里的receivePostInitialize()里调用此方法
	 * Use this method in receivePostInitialize() in your mod's starter class.
	 */
	public static void initialize() {
		new RelicSelectScreen(true, true) {
			@Override
			protected void addRelics() {
			}
			@Override
			protected void afterSelected() {
			}
			@Override
			protected void afterCanceled() {
			}
			@Override
			protected String categoryOf(AbstractRelic r) {
				return null;
			}
			@Override
			protected String descriptionOfCategory(String category) {
				return null;
			}
		};
	}

	private RelicSelectScreen(boolean forSubscriber, boolean bool) {
		subscriber = this;
		BaseMod.subscribe(subscriber);
	}
	
	// -------------------------------------------------------------
	
	public RelicSelectScreen() {
		this(null, true);
	}
	
	public RelicSelectScreen(boolean canSkip) {
		this(null, canSkip);
	}

	public RelicSelectScreen(Collection<? extends AbstractRelic> c) {
		this(c, true);
	}
	
	public RelicSelectScreen(String bDesc, String title, String desc) {
		this(true, bDesc, title, desc);
	}
	
	public RelicSelectScreen(boolean canSkip, String bDesc, String title, String desc) {
		this(null, canSkip, bDesc, title, desc);
	}
	
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip) {
		this(c, canSkip, null, null, null);
	}

	public RelicSelectScreen(boolean autoSort, int amountToSelect, boolean anyNum) {
		this(null, autoSort, amountToSelect, anyNum);
	}

	/**
	 * 构造方法，自动将构造出的实例赋值给静态变量screen以便于使用
	 * @param c 要显示的遗物集合
	 * @param canSkip 能否跳过
	 * @param bDesc 在选择界面下方的小字提醒
	 * @param title 遗物列表的标题
	 * @param desc 遗物列表的描述
	 */
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip, String bDesc, String title, String desc) {
		this(c, canSkip, bDesc, title, desc, false, 1, false);
	}
	
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip, boolean autoSort) {
		this(c, canSkip, null, null, null, autoSort, 1, false);
	}
	
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean autoSort, int amountToSelect) {
		this(c, autoSort, amountToSelect, false);
	}
	
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean autoSort, int amountToSelect,
			boolean anyNum) {
		this(c, false, null, null, null, autoSort, amountToSelect, anyNum);
	}
	
	/**
	 * 构造方法，自动将构造出的实例赋值给静态变量screen以便于使用
	 * @param c 要显示的遗物集合
	 * @param canSkip 能否跳过
	 * @param bDesc 在选择界面下方的小字提醒
	 * @param title 遗物列表的标题
	 * @param desc 遗物列表的描述
	 * @param autoSort 是否自动按设定分类方法分类
	 * @param amountToSelect 选择遗物数量
	 * @param anyNum 是否可以在选择一个遗物之后跳过剩下未选次数
	 */
	public RelicSelectScreen(Collection<? extends AbstractRelic> c, boolean canSkip, String bDesc, String title,
			String desc, boolean autoSort, int amountToSelect, boolean anyNum) {
		this.scrollBar = new ScrollBar(this);
		this.button = new ConfirmButton("跳过");
		this.button.isDisabled = !canSkip;
		this.setDescription(bDesc, title, desc);
		if (c != null)
			this.relics.addAll(c);
		isPrevMouseDown = false;
		screen = this;
		this.autoSort = autoSort;
		this.amountToSelect = amountToSelect;
		this.anyNum = anyNum;
	}
	
	/**
	 * 设置窗口的提示信息
	 * @param bDesc 在选择界面下方的小字提醒
	 * @param title 遗物列表的标题
	 * @param desc 遗物列表的描述
	 */
	public void setDescription(String bDesc, String title, String desc) {
		if (bDesc != null)
			this.bottomDesc = bDesc;
		if (title != null)
			this.infoTitle = title;
		if (desc != null)
			this.infoDesc = desc;
	}
	
	/**
	 * 开启当前选择窗口，可以在子类的构造方法的最后调用来达到自动开启的效果
	 */
	public static void openScreen() {
		screen.open();
	}
	
	/**
	 * 开启选择窗口，可以在子类的构造方法的最后调用来达到自动开启的效果，也可以在构造方法后直接调用
	 */
	public void open() {
		this.isOpen = true;
		if (this.relics.isEmpty())
			this.addRelics();
		if (this.autoSort)
			this.sort();
		this.button.show();
		this.scrollY = (Settings.HEIGHT - 400.0F * Settings.scale);
		CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.RELIC_VIEW;
	}

	/**
	 * 添加这个选遗物窗口里应该有的遗物
	 */
	protected abstract void addRelics();
	
	/**
	 * 当选完遗物后执行
	 */
	protected abstract void afterSelected();
	
	/**
	 * 当取消选择后执行
	 */
	protected abstract void afterCanceled();
	
	/**
	 * @param r 要进行分类的遗物
	 * @return 分类名，将会用于显示，例如按稀有度分类，这里应该返回 "普通"、"罕见" 等字符串
	 * 如果autoSort不会为true，此方法可以返回null
	 */
	protected abstract String categoryOf(AbstractRelic r);
	
	/**
	 * @param category 分类类别
	 * @return 分类描述，会显示在分类名下方，例如 "极为少见的独特且强大的遗物。"
	 * 如果autoSort不会为true，此方法可以返回null
	 */
	protected abstract String descriptionOfCategory(String category);
	
	/**
	 * 当选择完指定数量的遗物后执行
	 * 如果指定选择数量为1，不会执行，此方法可以留空
	 */
	protected void afterSelectedAll() {
	}
	
	protected void sort() {
		this.category.clear();
		this.sortedRelics.clear();
		for (AbstractRelic r : this.relics) {
			String c = this.categoryOf(r);
			if (this.category.contains(c)) {
				int index = this.category.indexOf(c);
				this.sortedRelics.get(index).add(r);
			} else {
				this.category.add(c);
				ArrayList<AbstractRelic> tmp = new ArrayList<AbstractRelic>();
				tmp.add(r);
				this.sortedRelics.add(tmp);
			}
		}
		this.scrollUpperBound = this.calculateScrollBound();
	}
	
	private float calculateScrollBound() {
		int rows = 0;
		for (int i = 0; i < this.sortedRelics.size(); i++) {
			rows += 3 + this.sortedRelics.get(i).size() / 10;
		}
		return START_Y + rows * SPACE;
	}
	
	private void update() {
		if (this.hoveredRelic != null) {
			CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
			if (InputHelper.justClickedLeft) {
				this.clickStartedRelic = this.hoveredRelic;
			}
			if ((InputHelper.justReleasedClickLeft) && (this.hoveredRelic == this.clickStartedRelic)) {
				this.selectedRelic = this.hoveredRelic;
				this.afterSelected();
				if (this.amountToSelect > 1) {
					this.selectedRelics.add(this.selectedRelic);
					if (this.amountToSelect == this.selectedRelics.size()) {
						this.afterSelectedAll();
						screen = null;
					} else if (this.anyNum && this.button.isDisabled)
						this.button.isDisabled = false;
				} else if (!rejectSelection) {
					screen = null;
				} else {
					rejectSelection = false;
				}
			}
		} else {
			this.clickStartedRelic = null;
		}
		if (!this.button.isDisabled) {
			this.button.update();
			if ((this.button.hb.clicked) || (InputHelper.pressedEscape)) {
				CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.NONE;
				screen = null;
				InputHelper.pressedEscape = false;
				this.button.hide();
				this.afterCanceled();
				CardCrawlGame.mainMenuScreen.panelScreen.refresh();
			}
		}
		if (!this.scrollBar.update())
			this.updateScrolling();
		InputHelper.justClickedLeft = false;
		this.hoveredRelic = null;
		if (this.autoSort)
			this.updateLists();
		else
			this.updateList(this.relics);
	}
	
	/**
	 * 刷新显示
	 */
	private static void updateRender(SpriteBatch sb) {
		if (screen != null && screen.isOpen) {
			sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.8F));
			sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
			screen.render(sb);
			Color c = new Color(1.0F, 1.0F, 1.0F, 0.0F);
			sb.setColor(c);
			sb.draw(ImageMaster.MAP_LEGEND, X - 256.0F, Y - 400.0F - DungeonMapScreen.offsetY / 50.0F, 256.0F, 400.0F,
					512.0F, 800.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
			Color c2 = new Color(MapRoomNode.AVAILABLE_COLOR.r, MapRoomNode.AVAILABLE_COLOR.g,
					MapRoomNode.AVAILABLE_COLOR.b, c.a);
			FontHelper.renderFontCentered(sb, FontHelper.menuBannerFont, MOD_ID, X,
					Y + 170.0F * Settings.scale - DungeonMapScreen.offsetY / 50.0F, c2);
			sb.setColor(c2);
			FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameFont, screen.bottomDesc, TEXT_X - 50.0F * Settings.scale,
					Y - SPACE_Y * 0.0F + 113.0F * Settings.scale - DungeonMapScreen.offsetY / 50.0F, c);
		}
	}
	
	public void receiveRender(SpriteBatch sb) {
		updateRender(sb);
	}
	
	public void receivePreUpdate() {
		updateScreen();
	}
	
	/**
	 * 如果屏幕处于打开状态，刷新鼠标点击选择、刷新屏幕
	 */
	private static void updateScreen() {
		if (screen != null && screen.isOpen) {
			InputHelper.isMouseDown = Gdx.input.isButtonPressed(0);
			if ((!isPrevMouseDown) && (InputHelper.isMouseDown)) {
				InputHelper.justClickedLeft = true;
				isPrevMouseDown = true;
			} else if ((isPrevMouseDown) && (!InputHelper.isMouseDown)) {
				InputHelper.justReleasedClickLeft = true;
				isPrevMouseDown = false;
			}
			screen.update();
		}
	}

	private void updateScrolling() {
		if (!this.isOpen)
			return;
		int y = InputHelper.mY;
		if (!this.grabbedScreen) {
			if (InputHelper.scrolledDown) {
				this.targetY += Settings.SCROLL_SPEED;
			} else if (InputHelper.scrolledUp) {
				this.targetY -= Settings.SCROLL_SPEED;
			}
			if (InputHelper.justClickedLeft) {
				this.grabbedScreen = true;
				this.grabStartY = (y - this.targetY);
			}
		} else if (InputHelper.isMouseDown) {
			this.targetY = (y - this.grabStartY);
		} else {
			this.grabbedScreen = false;
		}
		this.scrollY = MathHelper.scrollSnapLerpSpeed(this.scrollY, this.targetY);
		this.resetScrolling();
	    this.updateBarPosition();
	}
	
	private void resetScrolling() {
		resetScrolling(true, this.scrollLowerBound);
		resetScrolling(false, this.scrollUpperBound);
	}
	
	private void resetScrolling(boolean lower, float bound) {
		if ((lower && this.targetY < bound) || (!lower && this.targetY > bound))
			this.targetY = MathHelper.scrollSnapLerpSpeed(this.targetY, bound);
	}
	
	private void moveHitBox(AbstractRelic r) {
		r.hb.move(r.currentX, r.currentY);
	}
	
	private void updateList(ArrayList<AbstractRelic> list) {
		list.stream().peek(this::moveHitBox).peek(r -> r.update()).filter(r -> r.hb.hovered)
				.forEach(r -> this.hoveredRelic = r);
	}
	
	private void updateLists() {
		this.sortedRelics.forEach(this::updateList);
	}
	
	private void renderLists(SpriteBatch sb) {
		this.category.forEach(msg -> renderList(sb, msg, this.descriptionOfCategory(msg),
				this.sortedRelics.get(this.category.indexOf(msg))));
	}

	private void render(SpriteBatch sb) {
		this.row = -1;
		if (this.autoSort)
			this.renderLists(sb);
		else
			this.renderList(sb, this.infoTitle, this.infoDesc, this.relics);
		this.button.render(sb);
		this.scrollBar.render(sb);
	}
	
	private void setPosition(AbstractRelic r, ArrayList<AbstractRelic> list) {
		int i = list.indexOf(r);
		r.currentX = START_X + SPACE * (i % 10);
		r.currentY = (this.scrollY - SPACE * (i / 10 + this.row));
	}
	
	private Color getColor(AbstractRelic r) {
		if (RelicLibrary.redList.contains(r)) {
			return RED_OUTLINE_COLOR;
		} else if (RelicLibrary.greenList.contains(r)) {
			return GREEN_OUTLINE_COLOR;
		} else if (RelicLibrary.blueList.contains(r)) {
			return BLUE_OUTLINE_COLOR;
		} else if (RelicLibrary.whiteList.contains(r)) {
			return PURPLE_RELIC_COLOR;
		} else {
			return BLACK_OUTLINE_COLOR;
		}
	}

	private void render(SpriteBatch sb, AbstractRelic r) {
		r.render(sb, false, getColor(r));
	}
	
	private void renderList(SpriteBatch sb, String msg, String desc, ArrayList<AbstractRelic> list) {
		this.row += 2;
		FontHelper.renderSmartText(sb, FontHelper.buttonLabelFont, msg, START_X - 50.0F * Settings.scale,
				this.scrollY + 4.0F * Settings.scale - SPACE * this.row, 99999.0F, 0.0F, Settings.GOLD_COLOR);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, desc,
				START_X - 50.0F * Settings.scale
						+ FontHelper.getSmartWidth(FontHelper.buttonLabelFont, msg, 99999.0F, 0.0F),
				this.scrollY - 0.0F * Settings.scale - SPACE * this.row, 99999.0F, 0.0F, Settings.CREAM_COLOR);
		this.row += 1;
		list.stream().peek(r -> r.isSeen = true).peek(r -> setPosition(r, list)).forEach(r -> render(sb, r));
		this.row += list.size() / 10;
	}
	
	public void scrolledUsingBar(float newPercent) {
		float newPosition = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound,
				newPercent);
		this.scrollY = newPosition;
		this.targetY = newPosition;
		updateBarPosition();
	}

	private void updateBarPosition() {
		if (this.isOpen) {
			float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound,
					this.scrollY);
			this.scrollBar.parentScrolledToPercent(percent);
		}
	}
}