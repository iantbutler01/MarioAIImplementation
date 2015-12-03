package togepi;

import EnemyActorPhysics.*;
import ch.idsia.agents.controllers.modules.Entities;
import ch.idsia.agents.controllers.modules.Tiles;
import ch.idsia.benchmark.mario.engine.generalization.Entity;
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Core Class that contains the Graph and must be instantiated before actionSelectionAI() in your agent class.
 * The constructor takes two integers which correspond to the X and Y size of the grid you want to construct. Note, can be no larger than the perceptive grid, but can be smaller and non square e.g. 5x9.
 * Call generateGraph(Entities a, Tiles b, AgentType T) in actionSelectionAI() to generate the grid ON FIRST RUN
 */
public class GraphGenerator {
	/*@Nullable
	public marioDoll simMario = null;*/
	/*! X Size of Grid After Instantiation */
	public int gridSizeX = 0;
	/*! Y Size of Grid After Instantiation */
	public int gridSizeY = 0;
	/*! Hashmap of <Pair,Node> Type containing current Perceptive State */
	@Nullable
	public HashMap<Pair, Node> State = null;
	/*! Collection of <Node> Type containing current viewable field. Created from the HashMap */
	@Nullable
	public Collection<Node> List = null;
	/*! Boolean set to true after generateGraph() has been called the first time.*/
	public boolean isGraphGenerated = false;
	@Nullable
	private Entities e = null; /*! Contains Entities from current Perceptive Grid Sampling*/
	@Nullable
	private Tiles t = null; /*! Contains Tiles from current Perceptive Grid Sampling*/
	public GraphGenerator(int x, int y, MarioEntity marioClone) {
		gridSizeX = x;
		gridSizeY = y;
		//MarioEntity mario = marioClone;
		//simMario = new marioDoll(mario.speed.x, mario.speed.y, ((mario.mode.getCode() - 2) * -1)+1, 0, 0, mario.mayJump, mario.onGround);
	}
	/*public void generateSimMario(@NotNull MarioEntity mario) {
		simMario = new marioDoll(mario.speed.x, mario.speed.y, ((mario.mode.getCode() - 2) * -1)+1, 0, 0, mario.mayJump, mario.onGround);
		if(mario.onGround) simMario.jumpTime = 4;
		else simMario.jumpTime = 3;
	}*/

	@NotNull
	public static <key> HashMap<key, Node> mapCopy(@NotNull final HashMap<key, Node> hashMap) {
		HashMap<key, Node> copyMap = new HashMap<>();
		for (Map.Entry<key, Node> e : hashMap.entrySet()) {
			copyMap.put(e.getKey(), e.getValue().clone());
		}

		return copyMap;
	}

	/**
	 * Function call generate the graph. Takes 3 Arguments Current Entities, Current Tiles, and Agent-Type(Currently Only valid for A-Star).
	 * First creates all nodes in the given range (X by Y)
	 * Then links the nodes to their children/parents.
	 * Finally sets if a node is a goal state. Which currently is any node on the right edge of the graph that has no blocks or enemies.
	 */
	public void generateGraph(Entities a, Tiles b) {

		e = a;
		t = b;
		HashMap<Pair, Node> Graph = new HashMap<>();
		for (int i = -gridSizeX; i <= gridSizeX; i++) {
			for (int j = gridSizeY; j >= -gridSizeY; j--) {
				Node currentNode = new Node(i, j, 2 * gridSizeX, 2 * gridSizeY);
				if (i == 0 && j == 0) {
					currentNode.mario = true;
					currentNode.alterMario = new MyMario(0,0);
				}
				Graph.put(new Pair(i, j), currentNode);
			}
		}
		Collection<Node> listNodes = Graph.values();
		for (Node iterable : listNodes) {
			int y = iterable.yPos;
			int x = iterable.xPos;

			if (!((y - 1) < -gridSizeY)) {
				Node childUp = Graph.get(new Pair(iterable.xPos, iterable.yPos - 1)); //Example of using a pair to check the HashMap
				iterable.children.add(childUp);
			}
			if (!((y + 1) > gridSizeY)) {
				Node childDown = Graph.get(new Pair(iterable.xPos, iterable.yPos + 1));
				iterable.children.add(childDown);
			}
			if (!((x + 1) > gridSizeX)) {
				Node childForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos));
				iterable.children.add(childForward);
				if (!((y + 1) > gridSizeY)) {
					Node childUpForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos + 1));
					iterable.children.add(childUpForward);
				}
				if (!((y - 1) < -gridSizeY)) {
					Node childDownForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos - 1));
					iterable.children.add(childDownForward);
				}
			}
			if (!((x - 1) < -gridSizeX)) {
				Node childBackward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos));
				iterable.children.add(childBackward);
				if (!((y + 1) > gridSizeY)) {
					Node childUpForward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos + 1));
					iterable.children.add(childUpForward);
				}
				if (!((y - 1) < -gridSizeY)) {
					Node childDownForward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos - 1));
					iterable.children.add(childDownForward);
				}
			}
		}

		isGraphGenerated = true;
		State = Graph;
		List = State.values();
	}

	@NotNull
	public HashMap<Pair, Node> generateEmptyGraph() {

		HashMap<Pair, Node> Graph = new HashMap<>();
		for (int i = -gridSizeX; i <= gridSizeX; i++) {
			for (int j = gridSizeY; j >= -gridSizeY; j--) {
				Node currentNode = new Node(i, j);
				Graph.put(new Pair(i, j), currentNode);
			}
		}
		Collection<Node> listNodes = Graph.values();
		for (Node iterable : listNodes) {
			int y = iterable.yPos;
			int x = iterable.xPos;

			if (!((y - 1) < -gridSizeY)) {
				Node childUp = Graph.get(new Pair(iterable.xPos, iterable.yPos - 1)); //Example of using a pair to check the HashMap
				iterable.children.add(childUp);
			}
			if (!((y + 1) > gridSizeY)) {
				Node childDown = Graph.get(new Pair(iterable.xPos, iterable.yPos + 1));
				iterable.children.add(childDown);
			}
			if (!((x + 1) > gridSizeX)) {
				Node childForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos));
				iterable.children.add(childForward);
				if (!((y + 1) > gridSizeY)) {
					Node childUpForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos + 1));
					iterable.children.add(childUpForward);
				}
				if (!((y - 1) < -gridSizeY)) {
					Node childDownForward = Graph.get(new Pair(iterable.xPos + 1, iterable.yPos - 1));
					iterable.children.add(childDownForward);
				}
			}
			if (!((x - 1) < -gridSizeX)) {
				Node childBackward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos));
				iterable.children.add(childBackward);
				if (!((y + 1) > gridSizeY)) {
					Node childUpForward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos + 1));
					iterable.children.add(childUpForward);
				}
				if (!((y - 1) < -gridSizeY)) {
					Node childDownForward = Graph.get(new Pair(iterable.xPos - 1, iterable.yPos - 1));
					iterable.children.add(childDownForward);
				}
			}
		}
		return Graph;
	}

	/**
	 * Function call loops through List of nodes and updates them to current state.
	 */
	public void resetNodes(Entities a, Tiles b) {

		e = a;
		t = b;
		for (Node resetNode : List) {
			resetNode.reset();
		}
	}

	/*protected Pair marioMove(float x, float y, boolean left, boolean right, boolean speed) {
		simMario.xa = speed ? 1.2f : 0.6f;
		float xD = simMario.xa * 0.4f;
		x += left ? -xD : xD;
		return new Pair((int) x, (int) y);
	}*/

	/*@NotNull
	private Pair blockResMove(@NotNull Node marioNode, @NotNull HashMap<Pair,Node> state, boolean speed, boolean positive ) {
		int x = positive ? marioNode.xPos+1 : marioNode.xPos-1;
		if(x > 9) new Pair(9,marioNode.yPos);
		if(x < -9) new Pair(-9,marioNode.yPos);
		Pair newMarioPos = new Pair(x,marioNode.yPos);
		if(state.get(newMarioPos).blockHere) {
			Pair oldPos = new Pair(marioNode.xPos,marioNode.yPos);
			return oldPos;
		}
		if(speed) {
			if(positive) ++newMarioPos.x; else --newMarioPos.x;
			if (state.get(newMarioPos).blockHere) {
				Pair oldPos = new Pair(marioNode.xPos, marioNode.yPos);
				return oldPos;
			}
		}


		return newMarioPos;

	}*/

	/*public Pair approxMarioJump(float x, float y, float sX, float sY, boolean left, boolean right, boolean longJ) {
		double xD = 0;
		double yD = 0;
		float xJump = 0;
		float yJump = 0;
			if (simMario.jumpTime < 0) {
				simMario.xa = xJump;
				simMario.ya = -simMario.jumpTime * yJump;
				simMario.jumpTime++;
			} else if (simMario.onGround && simMario.mayJump) {
				xJump = 0;
				yJump = -1.9f;
				simMario.jumpTime = 7;
				simMario.ya = simMario.jumpTime * yJump;
				simMario.onGround = false;
			} else if (simMario.jumpTime > 0) {
				simMario.xa += xJump;
				simMario.ya = simMario.jumpTime * yJump;
				simMario.jumpTime--;
			} else {
				simMario.jumpTime = 0;
				simMario.onGround = true;
				simMario.mayJump = true;
			}
			yD += simMario.ya * 0.4 + 0.5 * (simMario.jumpTime) * 0.4;
			xD += simMario.xa * 0.4;


		x += left ? -xD : xD;
		y += yD;

		return new Pair((int) Math.ceil(x), (int) Math.ceil(y));
	}*/

	/*@Nullable
	private Pair blockResJump(@NotNull Node marioNode, @NotNull HashMap<Pair,Node> state) {
		Pair newMarioPos = null;
		Node willBeHere = null;
		if(simMario.jumpTime > 2) {
			newMarioPos = new Pair(marioNode.xPos, marioNode.yPos-1);
			simMario.onGround = false;
			--simMario.jumpTime;
		}
		else {
			newMarioPos = new Pair(marioNode.xPos, marioNode.yPos+1);
			simMario.mayJump = false;
			--simMario.jumpTime;
			if(simMario.jumpTime <= 0) {
				simMario.jumpTime = 4;
				simMario.onGround = true;
				simMario.mayJump = true;
			}
		}
		willBeHere = state.get(newMarioPos);
		return newMarioPos;
	}*/
	/*@Nullable
	private Pair blockResMoveJump(@NotNull Node marioNode, @NotNull HashMap<Pair,Node> state, boolean positive) {
		Pair newMarioPos = null;
		Node willBeHere = null;
		int x = marioNode.xPos;
		if(positive) ++x; else --x;
		if(simMario.jumpTime > 2) {
			newMarioPos = new Pair(x, marioNode.yPos-1);
			simMario.onGround = false;
			--simMario.jumpTime;
		}
		else {
			newMarioPos = new Pair(x, marioNode.yPos+1);
			simMario.mayJump = false;
			--simMario.jumpTime;
			if(simMario.jumpTime <= 0) {
				simMario.jumpTime = 4;
				simMario.onGround = true;
				simMario.mayJump = true;
			}
		}
		willBeHere = state.get(newMarioPos);
		return newMarioPos;
	}*/
	private boolean collisionDetect(@NotNull Node block) {
		return block.enemyHere;
	}

	@NotNull
	public Vector<genPair<Pair, genPair<Action, HashMap<Pair, Node>>>> tick(@NotNull HashMap<Pair, Node> cState, @NotNull Action[] possibleActions) {
		//tick =  40ms .040 seconds
		Vector<genPair<Pair, Entity>> existingEntities = new Vector<>();
		HashMap<Pair, Node> blankState = generateEmptyGraph();
		Vector<genPair<Pair, genPair<Action, HashMap<Pair, Node>>>> possibleStates = new Vector<>();
		Node marioNode = null;

		for (Map.Entry<Pair, Node> et : cState.entrySet()) {
			Node cNode = et.getValue();
			Pair pos = new Pair(cNode.xPos, cNode.yPos);
			List<Entity> cNodeEntities = e.entities(cNode.xPos, cNode.yPos);
			for (Entity x : cNodeEntities) {
				existingEntities.add(new genPair<>(pos, x));
			}

			//If the current node in the original state has blocks or a double block at this position so should the new one.
			blankState.get(new Pair(cNode.xPos, cNode.yPos)).blockHere = cNode.blockHere;
			blankState.get(new Pair(cNode.xPos, cNode.yPos)).doubleBlock = cNode.doubleBlock;

			if (cNode.mario) marioNode = cNode;

		}
		//Update the enemy positions
		for (genPair<Pair, Entity> e : existingEntities) {
			Node moveEntityTo = null;
			switch (e.y.type) {
				case FIREBALL:
					Fireball a = new Fireball(e.x.x, e.x.y, 1);
					a.ya = e.y.speed.y;
					a.xa = e.y.speed.x;
					a.tick();
					if(!(a.x < -9 || a.x > 9)) {
						moveEntityTo = blankState.get(new Pair(a.x, a.y));
						moveEntityTo.modelEntitiesHere.add(a);
					}
					break;
				case BULLET_BILL:
					BulletBill f = new BulletBill(e.x.x, e.x.y, -1);
					f.ya = e.y.speed.y;
					f.xa = e.y.speed.x;
					f.tick();
					if(!(f.x < -9 || f.x > 9)) {
						moveEntityTo = blankState.get(new Pair(f.x, f.y));
						moveEntityTo.modelEntitiesHere.add(f);
						moveEntityTo.enemyHere = true;
					}
					break;
				case ENEMY_FLOWER:
					FlowerEnemy g = new FlowerEnemy(e.x.x, e.x.y);
					g.ya = e.y.speed.y;
					g.xa = e.y.speed.x;
					g.tick();
					if(!(g.x < -9 || g.x > 9)) {
						moveEntityTo = blankState.get(new Pair(g.x, g.y));
						moveEntityTo.modelEntitiesHere.add(g);
						moveEntityTo.enemyHere = true;
					}

					break;
				default:
					MyEnemy h = new MyEnemy(e.x.x, e.x.y, -1, false, e.y.type);
					h.tick();
					if ((h.x < -9 || h.x > 9) || (h.y <-9 || h.y>9)) {
					} else {
						System.out.println(h.x+"--"+h.y);
						moveEntityTo = blankState.get(new Pair(h.x, h.y));
						moveEntityTo.modelEntitiesHere.add(h);
						moveEntityTo.enemyHere = true;
					}
					break;
			}
		}
		//Make copies of the new state for the number of possible actions mario can take and move mario based on a possible action
		for (Action possibleAction : possibleActions) {
			Pair newMarioPos = null;
			Pair oldMarioPos = new Pair(marioNode.xPos,marioNode.yPos);
			MyMario altRealityMario = marioNode.alterMario.clone();
			HashMap<Pair, Node> currPossibleState = mapCopy(blankState);
			switch (possibleAction) {
				case Jump:
					altRealityMario.myKeys.set(MarioKey.JUMP,altRealityMario.mayJump);
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case RightLongJump:
					altRealityMario.myKeys.set(MarioKey.JUMP,!altRealityMario.onGround);
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case LeftLongJump:
					altRealityMario.myKeys.set(MarioKey.JUMP,!altRealityMario.onGround);
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case Left:
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case Right:
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case RightSpeed:
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.myKeys.press(MarioKey.SPEED);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case LeftSpeed:
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.myKeys.press(MarioKey.SPEED);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
			}
			if (newMarioPos.y < -9 || newMarioPos.y > 9) continue;
			Node updatingMarioNode = currPossibleState.get(newMarioPos);
			Node changeOldPos = currPossibleState.get(oldMarioPos);
			changeOldPos.mario = false;
			updatingMarioNode.mario = true;
			updatingMarioNode.alterMario = altRealityMario;
			possibleStates.add(new genPair<Pair, genPair<Action, HashMap<Pair, Node>>>(newMarioPos,
					new genPair<Action, HashMap<Pair, Node>>(possibleAction, currPossibleState)));
		}

		return possibleStates;


	}

	@NotNull
	public Vector<genPair<Pair, genPair<Action, HashMap<Pair, Node>>>> tickModel(@NotNull HashMap<Pair, Node> cState, @NotNull Action[] possibleActions) {
		//tick =  40ms .040 seconds
		Vector<genPair<Pair, MySprite>> existingEntities = new Vector<>();
		HashMap<Pair, Node> blankState = generateEmptyGraph();
		Vector<genPair<Pair, genPair<Action, HashMap<Pair, Node>>>> possibleStates = new Vector<>();
		Node marioNode = null;

		for (Map.Entry<Pair, Node> et : cState.entrySet()) {
			Node cNode = et.getValue();
			Pair pos = new Pair(cNode.xPos, cNode.yPos);
			List<MySprite> cNodeEntities = cNode.modelEntitiesHere;
			for (MySprite x : cNodeEntities) {
				existingEntities.add(new genPair<>(pos, x));
			}

			//If the current node in the original state has blocks or a double block at this position so should the new one.
			blankState.get(new Pair(cNode.xPos, cNode.yPos)).blockHere = cNode.blockHere;
			blankState.get(new Pair(cNode.xPos, cNode.yPos)).doubleBlock = cNode.doubleBlock;

			if (cNode.mario) {
				marioNode = cNode;
			}

		}
		//Update the enemy positions
		for (genPair<Pair, MySprite> e : existingEntities) {
			Node moveEntityTo = null;
			switch (e.y.type) {
				case FIREBALL:
					Fireball a = new Fireball(e.x.x, e.x.y, 1);
					a.ya = e.y.ya;
					a.xa = e.y.xa;
					a.tick();
					moveEntityTo = blankState.get(new Pair(a.x, a.y));
					moveEntityTo.modelEntitiesHere.add(a);
					break;
				case BULLET_BILL:
					BulletBill f = (BulletBill) e.y;
					f.ya = e.y.ya;
					f.xa = e.y.xa;
					f.tick();
					moveEntityTo = blankState.get(new Pair(f.x, f.y));
					moveEntityTo.modelEntitiesHere.add(f);
					moveEntityTo.enemyHere = true;
					break;
				case ENEMY_FLOWER:
					FlowerEnemy g = (FlowerEnemy) e.y;
					g.ya = e.y.ya;
					g.xa = e.y.xa;
					g.tick();
					moveEntityTo = blankState.get(new Pair(g.x, g.y));
					moveEntityTo.modelEntitiesHere.add(g);
					moveEntityTo.enemyHere = true;
					break;
				default:
					MyEnemy h = (MyEnemy) e.y;
					h.tick();
					moveEntityTo = blankState.get(new Pair(h.x, h.y));
					moveEntityTo.modelEntitiesHere.add(h);
					moveEntityTo.enemyHere = true;
					break;
			}
		}
		//Make copies of the new state for the number of possible actions mario can take and move mario based on a possible action
		for (Action possibleAction : possibleActions) {
			Pair oldMarioPos = new Pair(marioNode.xPos,marioNode.yPos);
			Pair newMarioPos = null;
			MyMario altRealityMario = marioNode.alterMario.clone();
			HashMap<Pair, Node> currPossibleState = mapCopy(blankState);
			switch (possibleAction) {
				case Jump:
					altRealityMario.myKeys.set(MarioKey.JUMP,altRealityMario.mayJump);
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case RightLongJump:
					altRealityMario.myKeys.set(MarioKey.JUMP,!altRealityMario.onGround);
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case LeftLongJump:
					altRealityMario.myKeys.set(MarioKey.JUMP,!altRealityMario.onGround);
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case Left:
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case Right:
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case RightSpeed:
					altRealityMario.myKeys.press(MarioKey.RIGHT);
					altRealityMario.myKeys.press(MarioKey.SPEED);
					altRealityMario.facing = 1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
				case LeftSpeed:
					altRealityMario.myKeys.press(MarioKey.LEFT);
					altRealityMario.myKeys.press(MarioKey.SPEED);
					altRealityMario.facing = -1;
					altRealityMario.tick();
					newMarioPos = new Pair(altRealityMario.x,altRealityMario.y);
					break;
			}
			if (newMarioPos.y < -9 || newMarioPos.y > 9) continue;
			Node updatingMarioNode = currPossibleState.get(newMarioPos);
			Node updateOldPos = currPossibleState.get(oldMarioPos);
			updateOldPos.mario = false;
			updatingMarioNode.alterMario = altRealityMario;
			updatingMarioNode.mario = true;
			possibleStates.add(new genPair<Pair, genPair<Action, HashMap<Pair, Node>>>(newMarioPos,
					new genPair<Action, HashMap<Pair, Node>>(possibleAction, currPossibleState)));
		}

		return possibleStates;


	}

	public enum Action {
		Jump,
		RightLongJump,
		LeftLongJump,
		Right,
		RightSpeed,
		Left,
		LeftSpeed
	}

	/*public class marioDoll extends MyMario {
		int strikes = 0;


		public marioDoll(float xSpeed, float ySpeed, int marioSize, int xPos, int yPos, boolean marioOnGround, boolean marioMayJump) {
			xa = xSpeed;
			ya = ySpeed;
			strikes = marioSize;
			x = xPos;
			y = yPos;
			onGround = marioOnGround;
			mayJump = marioMayJump;
		}

		public marioDoll() {

		}
	}*/

	@SuppressWarnings("CloneDoesntCallSuperClone")
	public class Node {
		/**
		 * Node class containing all the information about this particular cell in the grid.
		 * Constructor Node(int x, int y, AgentType T, int sx, int sy)
		 */

		@NotNull
		public Vector<MySprite> modelEntitiesHere = new Vector<>();
		public int sizeX = 9;
		public int sizeY = 9;
		public MyMario alterMario = null;
		public boolean mario = false;
		public boolean Other = false;
		/*! Boolean true if enemy is in this cell. */
		public boolean enemyHere = false;
		/*! Boolean true if block is in this cell. */
		public boolean blockHere = false;
		/*! Boolean true if block is in this cell and the one above it. */
		public boolean doubleBlock = false;
		/*! Integer This nodes X Position set in generateGraph()*/
		public int xPos = 0;
		/*! Integer This nodes Y Position set in generateGraph()*/
		public int yPos = 0;
		/*! Node Optional for solution-chains(Pathing/Search Algorithms) the node after it in the chain. Set by you the coder.*/
		@Nullable
		public Node next = null;
		/*! Node Optional for solution-chains(Pathing/Search Algorithms) the node before it in the chain. Set by you the coder.*/
		@Nullable
		public Node prev = null;
		/*! Vector Type Node containing all the children of this node in the graph. Set by generateGraph().*/
		@NotNull
		public Vector<Node> children = new Vector<>();

		/**
		 * Node Constructor.
		 */
		public Node(int x, int y, int sX, int sY) {
			sizeX = sX;
			sizeY = sY;
			xPos = x;
			yPos = y;
			blockHere = t.brick(x, y);
			doubleBlock = (t.brick(x + 1, y) && t.brick(x, y - 1));

		}

		public Node(int x, int y) {
			xPos = x;
			yPos = y;
		}

		/**
		 * Function call updating the node to current state of perceptive grid.
		 * Can be called singly by Node.reset() or also called by GraphGenerator.resetNodes(Entities a, Tiles t)
		 */
		public void reset() {
			blockHere = t.brick(xPos, yPos);
			doubleBlock = (t.brick(xPos, yPos) && t.brick(xPos + 2, yPos - 1));
			enemyHere = e.danger(xPos, yPos);
		}

		@NotNull
		public Node clone() {
			Node copy = new Node(this.xPos, this.yPos, this.sizeX, this.sizeY);
			copy.blockHere = this.blockHere;
			copy.doubleBlock = this.doubleBlock;
			copy.enemyHere = this.enemyHere;
			copy.mario = this.mario;
			return copy;
		}
	}


}

/*
* TODO: Modify all the state updating code to take lists of entites found on the node, create a list of the same entities using our implementation of them and then redistribute them accordingly
 	Required Updates: Node Constructor and Reset Functions, tick()
 	Probably Need: A list containing the old entities a queue for redistribution, and intermediate list of partially contained entities, a final list of entities;

 */

