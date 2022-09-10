package testmod.patches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import testmod.utils.MiscMethods;
import testmod.utils.PatchyTrigger;

@SpirePatch(clz = com.megacrit.cardcrawl.core.CardCrawlGame.class, method = "render")
public class PatchyPatchPatch implements MiscMethods {
	private static final String[] ANNOTATION = { SpirePatch.class.getName(), SpirePatches.class.getName(),
			SpirePatch2.class.getName(), SpirePatches2.class.getName() };
	public static final String CCG = CardCrawlGame.class.getName();
	public static final String AD = AbstractDungeon.class.getName();
	public static final String RP = RoomPhase.class.getName();
	public static final String PT = PatchyTrigger.class.getName() + ".PT";
	public static final String VALID = PatchyTrigger.class.getName() + ".valid()";
	
	public static String get(String name) {
		String tmp = "{if(" + CCG + ".dungeon != null && " + AD + ".player != null && " + AD;
		tmp += ".currMapNode != null && " + AD + ".currMapNode.room != null && " + AD + ".currMapNode.room.phase == ";
		tmp += RP + ".COMBAT && " + VALID + "){" + PT + ".patchAttack(\"" + name + "\");}}";
		return tmp;
	}
	
	public static void Raw(CtBehavior ctBehavior) {
		ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
		Patcher.annotationDBMap.values().forEach(db -> {
			if (db == null)
				return;
			ArrayList<String> patchClasses = new ArrayList<String>();
			
			Arrays.stream(ANNOTATION).forEach(i -> {
				Map<String, Set<String>> a = db.getAnnotationIndex();
				if (a != null) {
					Set<String> s = a.get(i);
					if (s != null)
						patchClasses.addAll(s);
				}
			});
			
			patchClasses.stream().filter(cn -> cn != null).forEach(className -> {
				try {
					CtClass ctPatchClass = pool.get(className);
					Stream.of(ctPatchClass.getDeclaredMethods()).filter(i -> isPatch(i)).forEach(m -> {
						try {
							m.insertBefore(get(m.getLongName()));
						} catch (CannotCompileException e) {
							e.printStackTrace();
						}
					});
				} catch (NotFoundException e1) {
					e1.printStackTrace();
				}
			});
		});
    }

	private static boolean isPatch(CtMethod m) {
		return isPrefix(m) || isPostfix(m) || (!isLocator(m) && isInsert(m));
	}

	private static boolean isPrefix(CtMethod m) {
		return "Prefix".equals(m.getName()) || m.hasAnnotation(SpirePrefixPatch.class);
	}

	private static boolean isPostfix(CtMethod m) {
		return "Postfix".equals(m.getName()) || m.hasAnnotation(SpirePostfixPatch.class);
	}

	private static boolean isLocator(CtMethod m) {
		return "Locator".equals(m.getName()) || m.getName().contains("Locator");
	}

	private static boolean isInsert(CtMethod m) {
		return "Insert".equals(m.getName()) || m.hasAnnotation(SpireInsertPatch.class);
	}

}
