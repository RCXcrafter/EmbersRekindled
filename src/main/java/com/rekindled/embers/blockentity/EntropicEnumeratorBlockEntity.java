package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3i;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.upgrade.EntropicEnumeratorUpgrade;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class EntropicEnumeratorBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	//client variables
	public Cubie[][][] visualCube = new Cubie[2][2][2];
	public int previousMove = -10;


	public Cubie[][][] cube = {
			{
				{new Cubie(new Vector3d(-1, -1, -1), new Vector3d(1, 2, 3)), new Cubie(new Vector3d(-1, -1, 1), new Vector3d(1, 2, 4))},
				{new Cubie(new Vector3d(-1, 1, -1), new Vector3d(1, 5, 3)), new Cubie(new Vector3d(-1, 1, 1), new Vector3d(1, 5, 4))}
			},
			{
				{new Cubie(new Vector3d(1, -1, -1), new Vector3d(6, 2, 3)), new Cubie(new Vector3d(1, -1, 1), new Vector3d(6, 2, 4))},
				{new Cubie(new Vector3d(1, 1, -1), new Vector3d(6, 5, 3)), new Cubie(new Vector3d(1, 1, 1), new Vector3d(6, 5, 4))}
			}
	};

	public long nextMoveTime = -1;
	public Move[] moveQueue = new Move[0];
	public int[] offsetQueue = new int[0];
	public boolean solving;

	public static Random seededRand = new Random();

	public EntropicEnumeratorUpgrade upgrade;

	public EntropicEnumeratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ENTROPIC_ENUMERATOR_ENTITY.get(), pPos, pBlockState);
		upgrade = new EntropicEnumeratorUpgrade(this);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					String key = "cubie_" + x + "_" + y + "_" + z;
					if (nbt.contains(key)) {
						CompoundTag cubieTag = nbt.getCompound(key);
						cube[x][y][z] = new Cubie(cube[x][y][z].basePosition, cube[x][y][z].baseColors, new Quaterniond(cubieTag.getDouble("qx"), cubieTag.getDouble("qy"), cubieTag.getDouble("qz"), cubieTag.getDouble("qw")));
					}
				}
			}
		}
		if (nbt.contains("nextMoveTime"))
			nextMoveTime = nbt.getLong("nextMoveTime");
		if (nbt.contains("queue")) {
			ListTag queue = nbt.getList("queue", Tag.TAG_COMPOUND);
			moveQueue = new Move[queue.size()];
			offsetQueue = new int[queue.size()];
			for (int i = 0; i < queue.size(); i++) {
				moveQueue[i] = Move.valueOf(queue.getCompound(i).getString("move"));
				offsetQueue[i] = queue.getCompound(i).getInt("offset");
			}
		}
		solving = nbt.getBoolean("solving");
		previousMove = -10;
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					CompoundTag cubieTag = new CompoundTag();
					cubieTag.putDouble("qx", cube[x][y][z].rotation.x);
					cubieTag.putDouble("qy", cube[x][y][z].rotation.y);
					cubieTag.putDouble("qz", cube[x][y][z].rotation.z);
					cubieTag.putDouble("qw", cube[x][y][z].rotation.w);
					nbt.put("cubie_" + x + "_" + y + "_" + z, cubieTag);
				}
			}
		}
		if (nextMoveTime > 0)
			nbt.putLong("nextMoveTime", nextMoveTime);
		ListTag queue = new ListTag();
		for (int i = 0; i < moveQueue.length; i++) {
			CompoundTag entry = new CompoundTag();
			entry.putString("move", moveQueue[i].name());
			entry.putInt("offset", offsetQueue[i]);
			queue.add(i, entry);
		}
		nbt.put("queue", queue);
		nbt.putBoolean("solving", solving);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static final int GODS_NUMBER = 14;

	public void solveInefficient() { //this takes at least several hours to finish calculating a solution
		solving = true;
		//cut current queue short
		int currentMove = moveQueue.length - 1;
		long turnTicks = level.getGameTime() - nextMoveTime;
		for (int i = 0; i < moveQueue.length - 1; i++) {
			turnTicks -= offsetQueue[i];
			if (turnTicks < (float) offsetQueue[i + 1]) {
				currentMove = i;
				break;
			}
		}
		for (int i = 0; i <= currentMove; i++) {
			moveQueue[i].makeMove(cube);
		}
		nextMoveTime = level.getGameTime() + moveTime / 2;

		Cubie[][][] cubeClone = new Cubie[2][2][2];
		boolean done = false;
		Move[] moves = new Move[GODS_NUMBER];
		for (int i = 0; i < Math.pow(Move.quarterMoves.length, GODS_NUMBER); i++) {
			if (done)
				break;
			//create algorithm to try
			for (int j = 0; j < GODS_NUMBER; j++) {
				moves[j] = Move.quarterMoves[(i % (Move.quarterMoves.length * (j + 1))) / (j + 1)];
			}
			//clone current cube
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						cubeClone[x][y][z] = new Cubie(cube[x][y][z].basePosition, cube[x][y][z].baseColors, new Quaterniond(cube[x][y][z].rotation));
					}
				}
			}
			//try solve
			for (int k = 0; k < GODS_NUMBER; k++) {
				moves[k].makeMove(cubeClone);
				if (isSolved(cubeClone)) {
					moveQueue = new Move[k];
					for (int l = 0; l < k; l++) {
						moveQueue[l] = moves[l];
					}
					done = true;
					break;
				}
			}
		}
		offsetQueue = new int[moveQueue.length];
		for (int i = 0; i < moveQueue.length; i++) {
			offsetQueue[i] = moveTime;
		}
		offsetQueue[0] = 0;
		setChanged();
	}

	public static void updateColors(Cubie[][][] cube, Vector3d[][][] colors) {
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					colors[(int) (cube[x][y][z].getPos().x/2.0+1.0)][(int) (cube[x][y][z].getPos().y/2.0+1.0)][(int) (cube[x][y][z].getPos().z/2.0+1.0)] = cube[x][y][z].getColors();
				}
			}
		}
	}

	//algorithms for first face
	public static final Move[] B = { Move.B };
	public static final Move[] L = { Move.L };
	public static final Move[] L_ = { Move.L_ };
	public static final Move[] B_ = { Move.B_ };
	public static final Move[] U_ = { Move.U_ };
	public static final Move[] DB = { Move.D, Move.B };
	public static final Move[] D_B = { Move.D_, Move.B };
	public static final Move[] D2B = { Move.D2, Move.B };
	public static final Move[] LB_ = { Move.L, Move.B_ };
	public static final Move[] DLB_ = { Move.D, Move.L, Move.B_ };
	public static final Move[] D2LB_ = { Move.D2, Move.L, Move.B_ };
	public static final Move[] B2 = { Move.B2 };
	public static final Move[] L2 = { Move.L2 };
	public static final Move[] DB2 = { Move.D, Move.B2 };
	public static final Move[] D_B2 = { Move.D_, Move.B2 };
	public static final Move[] D2B2 = { Move.D2, Move.B2 };
	public static final Move[] L_B_ = { Move.L_, Move.B_ };
	public static final Move[] LD_B2 = { Move.L, Move.D_, Move.B2 };
	public static final Move[] BDL_U_ = { Move.B, Move.D, Move.L_, Move.U_ };
	public static final Move[] B2DL_U_ = { Move.B2, Move.D, Move.L_, Move.U_ };
	public static final Move[] L_U_ = { Move.L_, Move.U_ };
	public static final Move[] DL_U_ = { Move.D, Move.L_, Move.U_ };
	public static final Move[] LU_ = { Move.L, Move.U_ };
	public static final Move[] BLU_ = { Move.B, Move.L, Move.U_ };
	public static final Move[] DL = { Move.D, Move.L };
	public static final Move[] L2D_L = { Move.L2, Move.D_, Move.L };
	public static final Move[] DL2 = { Move.D, Move.L2 };
	public static final Move[] BL_B_ = { Move.B, Move.L_, Move.B_ };
	public static final Move[] D2L = { Move.D2, Move.L };
	public static final Move[] D2L2 = { Move.D2, Move.L2 };
	public static final Move[] D_L = { Move.D_, Move.L };
	public static final Move[] D_L2 = { Move.D_, Move.L2 };
	public static final Move[] D2L_U_ = { Move.D2, Move.L_, Move.U_ };
	public static final Move[] D_L_U_ = { Move.D_, Move.L_, Move.U_ };
	public static final Move[] F_DF = { Move.F_, Move.D, Move.F };
	public static final Move[] LDLD_L2 = { Move.L, Move.D, Move.L, Move.D_, Move.L2 };
	public static final Move[] F_D_F_DF2 = { Move.F_, Move.D_, Move.F_, Move.D, Move.F2 };
	public static final Move[] DL2D_L2 = { Move.D, Move.L2, Move.D_, Move.L2 };
	public static final Move[] LD_L_ = { Move.L, Move.D_, Move.L_ };
	public static final Move[] F_D2F = { Move.F_, Move.D2, Move.F };
	public static final Move[] F_D_F = { Move.F_, Move.D_, Move.F };
	public static final Move[] LDL_ = { Move.L, Move.D, Move.L_ };
	public static final Move[] L2D_L2 = { Move.L2, Move.D_, Move.L2 };
	public static final Move[] F2DF2 = { Move.F2, Move.D, Move.F2 };
	public static final Move[] D2F_DF = { Move.D2, Move.F_, Move.D, Move.F };
	public static final Move[] D_L2D_L2 = { Move.D_, Move.L2, Move.D_, Move.L2 };
	public static final Move[] D_LD2L_ = { Move.D_, Move.L, Move.D2, Move.L_ };
	public static final Move[] LD2L_ = { Move.L, Move.D2, Move.L_ };

	public static final Move[] U = { Move.U };
	public static final Move[] U2 = { Move.U2 };
	public static final Move[] D = { Move.D };
	public static final Move[] D_ = { Move.D_ };
	public static final Move[] D2 = { Move.D2 };

	//algorithms for second face
	public static final Move[] OLL_H = { Move.R2, Move.U2, Move.R, Move.U2, Move.R2 };
	public static final Move[] OLL_PI = { Move.R, Move.U2, Move.R2, Move.U_, Move.R2, Move.U_, Move.R2, Move.U2, Move.R };
	public static final Move[] OLL_ANTISUNE = { Move.R, Move.U2, Move.R_, Move.U_, Move.R, Move.U_, Move.R_ };
	public static final Move[] OLL_SUNE = { Move.R, Move.U, Move.R_, Move.U, Move.R, Move.U2, Move.R_ };
	public static final Move[] OLL_L = { Move.F, Move.R_, Move.F_, Move.R, Move.U, Move.R, Move.U_, Move.R_ };
	public static final Move[] OLL_T = { Move.R, Move.U, Move.R_, Move.U_, Move.R_, Move.F, Move.R, Move.F_ };
	public static final Move[] OLL_U = { Move.F, Move.R, Move.U, Move.R_, Move.U_, Move.F_ };

	//algorithms for sides
	public static final Move[] PBL_ADJ_ADJ = { Move.R2, Move.U_, Move.B2, Move.U2, Move.R2, Move.U_, Move.R2 }; //correct cubies at front
	public static final Move[] PBL_ADJ_DIAG = { Move.R, Move.U_, Move.R, Move.F2, Move.R_, Move.U, Move.R_ }; //correct upside down L on front
	public static final Move[] PBL_DIAG_ADJ = { Move.R_, Move.D, Move.R_, Move.F2, Move.R, Move.D_, Move.R }; //correct reverse L on front
	public static final Move[] PBL_DIAG_DIAG = { Move.L2, Move.B2, Move.R2 };
	public static final Move[] PBL_ADJ_U = { Move.R2, Move.F2, Move.R, Move.U, Move.R_, Move.F2, Move.R, Move.F_, Move.R }; //incorrect cubies on the left
	public static final Move[] PBL_ADJ_D = { Move.R2, Move.F2, Move.R_, Move.D_, Move.R, Move.F2, Move.R_, Move.F, Move.R_ }; //incorrect cubies on the left
	public static final Move[] PBL_DIAG_U = { Move.R, Move.U_, Move.R_, Move.U_, Move.F2, Move.U_, Move.R, Move.U, Move.R_, Move.U, Move.F2 }; //incorrect cubie front right
	public static final Move[] PBL_DIAG_D = { Move.R_, Move.D, Move.R, Move.D, Move.F2, Move.D, Move.R_, Move.D_, Move.R, Move.D_, Move.F2 }; //incorrect cubie front right

	public void solve() {
		solving = true;
		//cut current queue short
		int currentMove = moveQueue.length - 1;
		long turnTicks = level.getGameTime() - nextMoveTime;
		for (int i = 0; i < moveQueue.length - 1; i++) {
			turnTicks -= offsetQueue[i];
			if (turnTicks < (float) offsetQueue[i + 1]) {
				currentMove = i;
				break;
			}
		}
		for (int i = 0; i <= currentMove; i++) {
			moveQueue[i].makeMove(cube);
		}

		//clone current cube
		Cubie[][][] cubeClone = new Cubie[2][2][2];
		Vector3d[][][] colors = new Vector3d[2][2][2];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					cubeClone[x][y][z] = new Cubie(cube[x][y][z].basePosition, cube[x][y][z].baseColors, new Quaterniond(cube[x][y][z].rotation));
					colors[(int) (cube[x][y][z].getPos().x/2.0+1.0)][(int) (cube[x][y][z].getPos().y/2.0+1.0)][(int) (cube[x][y][z].getPos().z/2.0+1.0)] = cube[x][y][z].getColors();
				}
			}
		}
		ArrayList<Move[]> algorithms = new ArrayList<Move[]>();

		//make the top face
		double topColor = colors[0][1][0].y;
		//second upper face
		//at least three of these are true so the last two are not actually needed
		if (colors[0][1][1].y != topColor) { //max 3 moves added
			if (colors[0][0][1].x == topColor) {
				algorithms.add(B);
				Move.makeMoves(cubeClone, B);
			} else if (colors[1][1][1].x == topColor) {
				algorithms.add(B_);
				Move.makeMoves(cubeClone, B_);
			} else if (colors[1][1][0].y == topColor) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
			} else if (colors[1][0][1].y == topColor) {
				algorithms.add(B2);
				Move.makeMoves(cubeClone, B2);
			} else if (colors[1][0][1].x == topColor) {
				algorithms.add(LB_);
				Move.makeMoves(cubeClone, LB_);
			} else if (colors[1][1][0].x == topColor) {
				algorithms.add(L_B_);
				Move.makeMoves(cubeClone, L_B_);
			} else if (colors[1][0][1].z == topColor) {
				algorithms.add(D_B);
				Move.makeMoves(cubeClone, D_B);
			} else if (colors[0][0][0].z == topColor) {
				algorithms.add(DB);
				Move.makeMoves(cubeClone, DB);
			} else if (colors[1][0][0].z == topColor) {
				algorithms.add(L_U_);
				Move.makeMoves(cubeClone, L_U_);
			} else if (colors[1][1][1].z == topColor) {
				algorithms.add(LU_);
				Move.makeMoves(cubeClone, LU_);
			} else if (colors[0][0][1].y == topColor) {
				algorithms.add(DB2);
				Move.makeMoves(cubeClone, DB2);
			} else if (colors[1][0][0].x == topColor) {
				algorithms.add(D2B);
				Move.makeMoves(cubeClone, D2B);
			} else if (colors[1][0][0].y == topColor) {
				algorithms.add(D_B2);
				Move.makeMoves(cubeClone, D_B2);
			} else if (colors[0][0][0].y == topColor) {
				algorithms.add(D2B2);
				Move.makeMoves(cubeClone, D2B2);
			} else if (colors[0][1][1].z == topColor) {
				algorithms.add(BLU_); //da ba dee
				Move.makeMoves(cubeClone, BLU_);
			} else if (colors[0][0][1].z == topColor) {
				algorithms.add(DLB_);
				Move.makeMoves(cubeClone, DLB_);
			} else if (colors[1][1][0].z == topColor) {
				algorithms.add(LD_B2);
				Move.makeMoves(cubeClone, LD_B2);
			} else if (colors[0][0][0].x == topColor) {
				algorithms.add(D2LB_);
				Move.makeMoves(cubeClone, D2LB_);
			} else if (colors[1][1][1].y == topColor) {
				algorithms.add(BDL_U_);
				Move.makeMoves(cubeClone, BDL_U_);
			} else if (colors[0][1][1].x == topColor) {
				algorithms.add(B2DL_U_);
				Move.makeMoves(cubeClone, B2DL_U_);
			}
			updateColors(cubeClone, colors);
		}

		//third upper face
		//at least two of these are true so the last one is not actually needed
		if (colors[1][1][1].y != topColor) { //max 3 moves added
			if (colors[1][0][1].z == topColor) {
				algorithms.add(L);
				Move.makeMoves(cubeClone, L);
			} else if (colors[1][1][0].z == topColor) {
				algorithms.add(L_);
				Move.makeMoves(cubeClone, L_);
			} else if (colors[1][0][0].y == topColor) {
				algorithms.add(L2);
				Move.makeMoves(cubeClone, L2);
			} else if (colors[1][1][0].y == topColor) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
			} else if (colors[0][0][1].x == topColor) {
				algorithms.add(DL);
				Move.makeMoves(cubeClone, DL);
			} else if (colors[1][0][0].z == topColor) {
				algorithms.add(L_U_);
				Move.makeMoves(cubeClone, L_U_);
			} else if (colors[1][1][1].z == topColor) {
				algorithms.add(LU_);
				Move.makeMoves(cubeClone, LU_);
			} else if (colors[1][0][0].x == topColor) {
				algorithms.add(D_L);
				Move.makeMoves(cubeClone, D_L);
			} else if (colors[0][0][0].y == topColor) {
				algorithms.add(D_L2);
				Move.makeMoves(cubeClone, D_L2);
			} else if (colors[1][0][1].y == topColor) {
				algorithms.add(DL2);
				Move.makeMoves(cubeClone, DL2);
			} else if (colors[0][0][0].z == topColor) {
				algorithms.add(D2L);
				Move.makeMoves(cubeClone, D2L);
			} else if (colors[0][0][1].y == topColor) {
				algorithms.add(D2L2);
				Move.makeMoves(cubeClone, D2L2);
			} else if (colors[1][1][0].x == topColor) {
				algorithms.add(BL_B_); //this one looks cool
				Move.makeMoves(cubeClone, BL_B_);
			} else if (colors[1][0][1].x == topColor) {
				algorithms.add(DL_U_);
				Move.makeMoves(cubeClone, DL_U_);
			} else if (colors[0][0][0].x == topColor) {
				algorithms.add(D_L_U_);
				Move.makeMoves(cubeClone, D_L_U_);
			} else if (colors[1][1][1].x == topColor) {
				algorithms.add(L2D_L);
				Move.makeMoves(cubeClone, L2D_L);
			} else if (colors[0][0][1].z == topColor) {
				algorithms.add(D2L_U_);
				Move.makeMoves(cubeClone, D2L_U_);
			}
			updateColors(cubeClone, colors);
		}

		//fourth upper face
		if (colors[1][1][0].y != topColor) { //max 5 moves added
			if (colors[1][0][1].z == topColor) {
				algorithms.add(F_DF);
				Move.makeMoves(cubeClone, F_DF);
			} else if (colors[1][1][0].z == topColor) {
				algorithms.add(LDLD_L2);
				Move.makeMoves(cubeClone, LDLD_L2);
			} else if (colors[1][0][0].y == topColor) {
				algorithms.add(DL2D_L2);
				Move.makeMoves(cubeClone, DL2D_L2);
			} else if (colors[0][0][1].x == topColor) {
				algorithms.add(F_D2F);
				Move.makeMoves(cubeClone, F_D2F);
			} else if (colors[1][0][0].z == topColor) {
				algorithms.add(F_D_F);
				Move.makeMoves(cubeClone, F_D_F);
			} else if (colors[1][0][0].x == topColor) {
				algorithms.add(LDL_);
				Move.makeMoves(cubeClone, LDL_);
			} else if (colors[0][0][0].y == topColor) {
				algorithms.add(L2D_L2);
				Move.makeMoves(cubeClone, L2D_L2);
			} else if (colors[1][0][1].y == topColor) {
				algorithms.add(F2DF2);
				Move.makeMoves(cubeClone, F2DF2);
			} else if (colors[0][0][0].z == topColor) {
				algorithms.add(D2F_DF);
				Move.makeMoves(cubeClone, D2F_DF);
			} else if (colors[0][0][1].y == topColor) {
				algorithms.add(D_L2D_L2);
				Move.makeMoves(cubeClone, D_L2D_L2);
			} else if (colors[1][1][0].x == topColor) {
				algorithms.add(F_D_F_DF2);
				Move.makeMoves(cubeClone, F_D_F_DF2);
			} else if (colors[1][0][1].x == topColor) {
				algorithms.add(D_LD2L_);
				Move.makeMoves(cubeClone, D_LD2L_);
			} else if (colors[0][0][0].x == topColor) {
				algorithms.add(LD_L_);
				Move.makeMoves(cubeClone, LD_L_);
			} else if (colors[0][0][1].z == topColor) {
				algorithms.add(LD2L_);
				Move.makeMoves(cubeClone, LD2L_);
			}
			updateColors(cubeClone, colors);
		}
		//top face complete!

		topColor = 7 - topColor;
		int correctFaces = 0;
		for (int x = 0; x < 2; x++) {
			for (int z = 0; z < 2; z++) {
				if (colors[x][0][z].y == topColor)
					correctFaces++;
			}
		}

		//move top color to bottom if needed
		if (correctFaces != 4) {
			algorithms.add(PBL_DIAG_DIAG); //3 moves added
			Move.makeMoves(cubeClone, PBL_DIAG_DIAG);
			updateColors(cubeClone, colors);
		}

		//make the new top face
		if (correctFaces == 0) { //max 10 moves added
			if (colors[0][1][0].z == topColor && colors[1][1][0].z == topColor) {
				if (colors[0][1][1].z == topColor) {
					algorithms.add(OLL_H);
					Move.makeMoves(cubeClone, OLL_H);
				} else {
					algorithms.add(U);
					Move.makeMoves(cubeClone, U);
					algorithms.add(OLL_PI);
					Move.makeMoves(cubeClone, OLL_PI);
				}
			} else if (colors[0][1][0].x == topColor && colors[0][1][1].x == topColor) {
				if (colors[1][1][0].x == topColor) {
					algorithms.add(U_);
					Move.makeMoves(cubeClone, U_);
					algorithms.add(OLL_H);
					Move.makeMoves(cubeClone, OLL_H);
				} else {
					algorithms.add(U2);
					Move.makeMoves(cubeClone, U2);
					algorithms.add(OLL_PI);
					Move.makeMoves(cubeClone, OLL_PI);
				}
			} else if (colors[0][1][1].z == topColor && colors[1][1][1].z == topColor) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
				algorithms.add(OLL_PI);
				Move.makeMoves(cubeClone, OLL_PI);
			} else {
				algorithms.add(OLL_PI);
				Move.makeMoves(cubeClone, OLL_PI);
			}
		} else if (correctFaces == 1) {
			if (colors[0][1][0].y == topColor) {
				if (colors[0][1][1].x == topColor) {
					algorithms.add(U);
					Move.makeMoves(cubeClone, U);
					algorithms.add(OLL_SUNE);
					Move.makeMoves(cubeClone, OLL_SUNE);
				} else {
					algorithms.add(U_);
					Move.makeMoves(cubeClone, U_);
					algorithms.add(OLL_ANTISUNE);
					Move.makeMoves(cubeClone, OLL_ANTISUNE);
				}
			} else if (colors[0][1][1].y == topColor) {
				if (colors[1][1][1].z == topColor) {
					algorithms.add(U2);
					Move.makeMoves(cubeClone, U2);
					algorithms.add(OLL_SUNE);
					Move.makeMoves(cubeClone, OLL_SUNE);
				} else {
					algorithms.add(OLL_ANTISUNE);
					Move.makeMoves(cubeClone, OLL_ANTISUNE);
				}
			} else if (colors[1][1][1].y == topColor) {
				if (colors[1][1][0].x == topColor) {
					algorithms.add(U_);
					Move.makeMoves(cubeClone, U_);
					algorithms.add(OLL_SUNE);
					Move.makeMoves(cubeClone, OLL_SUNE);
				} else {
					algorithms.add(U);
					Move.makeMoves(cubeClone, U);
					algorithms.add(OLL_ANTISUNE);
					Move.makeMoves(cubeClone, OLL_ANTISUNE);
				}
			} else if (colors[1][1][0].y == topColor) {
				if (colors[0][1][0].z == topColor) {
					algorithms.add(OLL_SUNE);
					Move.makeMoves(cubeClone, OLL_SUNE);
				} else {
					algorithms.add(U2);
					Move.makeMoves(cubeClone, U2);
					algorithms.add(OLL_ANTISUNE);
					Move.makeMoves(cubeClone, OLL_ANTISUNE);
				}
			}
		} else if (correctFaces == 2) {
			if (colors[0][1][0].y == topColor) {
				if (colors[0][1][1].y == topColor) {
					if (colors[1][1][0].z == topColor) {
						algorithms.add(OLL_T);
						Move.makeMoves(cubeClone, OLL_T);
					} else {
						algorithms.add(OLL_U);
						Move.makeMoves(cubeClone, OLL_U);
					}
				} else if (colors[1][1][0].y == topColor) {
					algorithms.add(U_);
					Move.makeMoves(cubeClone, U_);
					if (colors[1][1][1].x == topColor) {
						algorithms.add(OLL_T);
						Move.makeMoves(cubeClone, OLL_T);
					} else {
						algorithms.add(OLL_U);
						Move.makeMoves(cubeClone, OLL_U);
					}
				} else if (colors[1][1][1].y == topColor) {
					if (colors[1][1][0].z == topColor) {
						algorithms.add(OLL_L);
						Move.makeMoves(cubeClone, OLL_L);
					} else {
						algorithms.add(U2);
						Move.makeMoves(cubeClone, U2);
						algorithms.add(OLL_L);
						Move.makeMoves(cubeClone, OLL_L);
					}
				}
			} else if (colors[1][1][1].y == topColor) {
				if (colors[0][1][1].y == topColor) {
					algorithms.add(U);
					Move.makeMoves(cubeClone, U);
					if (colors[1][1][0].x == topColor) {
						algorithms.add(OLL_T);
						Move.makeMoves(cubeClone, OLL_T);
					} else {
						algorithms.add(OLL_U);
						Move.makeMoves(cubeClone, OLL_U);
					}
				} else if (colors[1][1][0].y == topColor) {
					algorithms.add(U2);
					Move.makeMoves(cubeClone, U2);
					if (colors[0][1][0].z == topColor) {
						algorithms.add(OLL_T);
						Move.makeMoves(cubeClone, OLL_T);
					} else {
						algorithms.add(OLL_U);
						Move.makeMoves(cubeClone, OLL_U);
					}
				}
			} else {
				if (colors[0][1][0].z == topColor) {
					algorithms.add(U_);
					Move.makeMoves(cubeClone, U_);
					algorithms.add(OLL_L);
					Move.makeMoves(cubeClone, OLL_L);
				} else {
					algorithms.add(U);
					Move.makeMoves(cubeClone, U);
					algorithms.add(OLL_L);
					Move.makeMoves(cubeClone, OLL_L);
				}
			}
		}

		if (correctFaces != 4) {
			updateColors(cubeClone, colors);
		}
		//top and bottom face complete!

		//solve the sides
		PBLState topState;
		PBLState bottomState;

		if (colors[0][1][0].z == colors[1][1][0].z) {
			if (colors[0][1][1].z == colors[1][1][1].z) {
				topState = PBLState.SOLVED;
			} else {
				topState = PBLState.ADJ;
			}
		} else if (colors[0][1][1].z == colors[1][1][1].z || colors[0][1][0].x == colors[0][1][1].x || colors[1][1][0].x == colors[1][1][1].x) {
			topState = PBLState.ADJ;
		} else {
			topState = PBLState.DIAG;
		}
		if (colors[0][0][0].z == colors[1][0][0].z) {
			if (colors[0][0][1].z == colors[1][0][1].z) {
				bottomState = PBLState.SOLVED;
			} else {
				bottomState = PBLState.ADJ;
			}
		} else if (colors[0][0][1].z == colors[1][0][1].z || colors[0][0][0].x == colors[0][0][1].x || colors[1][0][0].x == colors[1][0][1].x) {
			bottomState = PBLState.ADJ;
		} else {
			bottomState = PBLState.DIAG;
		}

		if (topState == PBLState.ADJ && bottomState == PBLState.ADJ) {//max 13 moves added
			if(colors[0][1][0].x == colors[0][1][1].x) {
				algorithms.add(U);
				Move.makeMoves(cubeClone, U);
			} else if(colors[1][1][0].x == colors[1][1][1].x) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
			} else if(colors[0][1][1].z == colors[1][1][1].z) {
				algorithms.add(U2);
				Move.makeMoves(cubeClone, U2);
			}
			if(colors[0][0][0].x == colors[0][0][1].x) {
				algorithms.add(D_);
				Move.makeMoves(cubeClone, D_);
			} else if(colors[1][0][0].x == colors[1][0][1].x) {
				algorithms.add(D);
				Move.makeMoves(cubeClone, D);
			} else if(colors[0][0][1].z == colors[1][0][1].z) {
				algorithms.add(D2);
				Move.makeMoves(cubeClone, D2);
			}
			algorithms.add(PBL_ADJ_ADJ);
			Move.makeMoves(cubeClone, PBL_ADJ_ADJ);
		} else if (topState == PBLState.ADJ && bottomState == PBLState.DIAG) {
			if (colors[0][1][0].x == colors[0][1][1].x) {
				algorithms.add(U);
				Move.makeMoves(cubeClone, U);
			} else if(colors[1][1][0].x == colors[1][1][1].x) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
			} else if(colors[0][1][1].z == colors[1][1][1].z) {
				algorithms.add(U2);
				Move.makeMoves(cubeClone, U2);
			}
			algorithms.add(PBL_ADJ_DIAG);
			Move.makeMoves(cubeClone, PBL_ADJ_DIAG);
		} else if (topState == PBLState.DIAG && bottomState == PBLState.ADJ) {
			if(colors[0][0][0].x == colors[0][0][1].x) {
				algorithms.add(D_);
				Move.makeMoves(cubeClone, D_);
			} else if(colors[1][0][0].x == colors[1][0][1].x) {
				algorithms.add(D);
				Move.makeMoves(cubeClone, D);
			} else if(colors[0][0][1].z == colors[1][0][1].z) {
				algorithms.add(D2);
				Move.makeMoves(cubeClone, D2);
			}
			algorithms.add(PBL_DIAG_ADJ);
			Move.makeMoves(cubeClone, PBL_DIAG_ADJ);
		} else if (topState == PBLState.DIAG && bottomState == PBLState.DIAG) {
			algorithms.add(PBL_DIAG_DIAG);
			Move.makeMoves(cubeClone, PBL_DIAG_DIAG);
		} else if (topState == PBLState.ADJ && bottomState == PBLState.SOLVED) {
			if (colors[0][1][0].z == colors[1][1][0].z) {
				algorithms.add(U_);
				Move.makeMoves(cubeClone, U_);
			} else if(colors[1][1][0].x == colors[1][1][1].x) {
				algorithms.add(U2);
				Move.makeMoves(cubeClone, U2);
			} else if(colors[0][1][1].z == colors[1][1][1].z) {
				algorithms.add(U);
				Move.makeMoves(cubeClone, U);
			}
			algorithms.add(PBL_ADJ_U);
			Move.makeMoves(cubeClone, PBL_ADJ_U);
		} else if (topState == PBLState.SOLVED && bottomState == PBLState.ADJ) {
			if (colors[0][0][0].z == colors[1][0][0].z) {
				algorithms.add(D);
				Move.makeMoves(cubeClone, D);
			} else if(colors[1][0][0].x == colors[1][0][1].x) {
				algorithms.add(D2);
				Move.makeMoves(cubeClone, D2);
			} else if(colors[0][0][1].z == colors[1][0][1].z) {
				algorithms.add(D_);
				Move.makeMoves(cubeClone, D_);
			}
			algorithms.add(PBL_ADJ_D);
			Move.makeMoves(cubeClone, PBL_ADJ_D);
		} else if (topState == PBLState.DIAG && bottomState == PBLState.SOLVED) {
			algorithms.add(PBL_DIAG_U);
			Move.makeMoves(cubeClone, PBL_DIAG_U);
		} else if (topState == PBLState.SOLVED && bottomState == PBLState.DIAG) {
			algorithms.add(PBL_DIAG_D);
			Move.makeMoves(cubeClone, PBL_DIAG_D);
		}
		if (topState != PBLState.SOLVED || bottomState != PBLState.SOLVED) {
			updateColors(cubeClone, colors);
		}
		//sides solved

		//finally, align top and bottom layer
		if (colors[0][0][0].z == colors[0][1][1].x) {//max 1 move added
			algorithms.add(U);
			//no need to turn the cube clone since it won't be used after this
		} else if(colors[0][0][0].z == colors[1][1][0].x) {
			algorithms.add(U_);
		} else if(colors[0][0][0].z == colors[1][1][1].z) {
			algorithms.add(U2);
		}
		//cube solved in max 38 moves!

		//put algorithms into the queue
		int moveCount = 0;
		for (Move[] algorithm : algorithms) {
			moveCount += algorithm.length;
		}
		moveQueue = new Move[moveCount];
		int moveIndex = 0;
		for (int i = 0; i < algorithms.size(); i++) {
			Move[] algorithm = algorithms.get(i);
			for (int j = 0; j < algorithm.length; j++) {
				moveQueue[moveIndex] = algorithm[j];
				moveIndex++;
			}
		}

		moveQueue = optimizeAlgorithm(moveQueue);

		offsetQueue = new int[moveQueue.length];
		for (int i = 0; i < moveQueue.length; i++) {
			offsetQueue[i] = solvingMoveTime;
		}
		if (moveQueue.length > 0)
			offsetQueue[0] = 0;

		//alchemy takes about 400 ticks
		nextMoveTime = level.getGameTime() + 390 + seededRand.nextInt(solvingMoveTime) - moveQueue.length * solvingMoveTime;
		setChanged();
	}

	public void restartScramble() {
		if (moveQueue.length > 0) {
			for (int i = 0; i < moveQueue.length; i++) {
				moveQueue[i].makeMove(cube);
			}
		}
		moveQueue = new Move[0];
		offsetQueue = new int[0];
		solving = false;
		setChanged();
	}

	//simply merges consecutive moves on the same layer
	public static Move[] optimizeAlgorithm(Move[] algorithm) {
		ArrayList<Move> newAlgorithm = new ArrayList<Move>();
		for (int i = 0; i < algorithm.length; i++) {
			if (i == algorithm.length - 1) {
				newAlgorithm.add(algorithm[i]);
				break;
			}
			String current = algorithm[i].name();
			String next = algorithm[i + 1].name();
			if (current.charAt(0) == next.charAt(0)) {
				char currentMod = current.length() > 1 ? current.charAt(1) : ' ';
				char nextMod = next.length() > 1 ? next.charAt(1) : ' ';
				if ((currentMod == '_' && nextMod == '_') || (currentMod == ' ' && nextMod == ' ')) {
					newAlgorithm.add(Move.valueOf(current.charAt(0) + "2"));
				} else if ((currentMod == '2' && nextMod == '_') || (currentMod == '_' && nextMod == '2')) {
					newAlgorithm.add(Move.valueOf(current.charAt(0) + ""));
				} else if ((currentMod == '2' && nextMod == ' ') || (currentMod == ' ' && nextMod == '2')) {
					newAlgorithm.add(Move.valueOf(current.charAt(0) + "_"));
				}
				i++;
			} else {
				newAlgorithm.add(algorithm[i]);
			}
		}
		if (newAlgorithm.size() < algorithm.length) {
			return newAlgorithm.toArray(new Move[newAlgorithm.size()]);
		}
		return algorithm;
	}

	public static boolean isSolved(Cubie[][][] cube) {
		Quaterniond base = cube[1][1][1].rotation;
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					if (!cube[x][y][z].rotation.equals(base, 0.1))
						return false;
				}
			}
		}
		return true;
	}

	public static int solvingMoveTime = 4;
	public static int moveTime = 13;
	public static int queueSize = 10;
	public static int queueTime = moveTime * queueSize;

	public static void serverTick(Level level, BlockPos pos, BlockState state, EntropicEnumeratorBlockEntity blockEntity) {
		if (blockEntity.solving)
			return;
		seededRand.setSeed(pos.asLong());
		seededRand.nextInt();
		seededRand.nextInt();
		seededRand.nextInt();
		seededRand.nextInt();
		seededRand.nextInt();
		seededRand.nextInt();
		seededRand.nextInt(); //for some reason the rand generates better numbers if you warm it up a little first
		if (level.getGameTime() % queueTime == seededRand.nextInt(queueTime)) {
			Move lastMove = null;
			if (blockEntity.moveQueue.length > 0) {
				for (int i = 0; i < blockEntity.moveQueue.length; i++) {
					blockEntity.moveQueue[i].makeMove(blockEntity.cube);
				}
				lastMove = blockEntity.moveQueue[blockEntity.moveQueue.length - 1];
			}

			blockEntity.nextMoveTime = level.getGameTime() + moveTime / 2;
			blockEntity.moveQueue = new Move[queueSize];
			blockEntity.offsetQueue = new int[queueSize];

			for (int i = 0; i < queueSize; i++) {
				Move[] nextMoves = Move.getNextMoves(lastMove);
				blockEntity.moveQueue[i] = nextMoves[Misc.random.nextInt(nextMoves.length)];
				blockEntity.offsetQueue[i] = moveTime;
				lastMove = blockEntity.moveQueue[i];
			}
			blockEntity.offsetQueue[0] = 0;
			blockEntity.setChanged();
		}
	}

	@SuppressWarnings("resource")
	public static void clientTick(Level level, BlockPos pos, BlockState state, EntropicEnumeratorBlockEntity blockEntity) {
		if (level.getGameTime() >= blockEntity.nextMoveTime) {
			long turnTicks = level.getGameTime() - blockEntity.nextMoveTime;
			for (int i = 0; i < blockEntity.moveQueue.length; i++) {
				if (turnTicks == blockEntity.offsetQueue[i]) {
					level.playSound(Minecraft.getInstance().player, pos, EmbersSounds.ENTROPIC_ENUMERATOR_TURN.get(), SoundSource.BLOCKS, blockEntity.solving ? 0.7f : 0.5f, blockEntity.solving ? 1.2f : 0.8f);
					break;
				}
				if (turnTicks < blockEntity.offsetQueue[i])
					break;
				turnTicks -= blockEntity.offsetQueue[i];
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && (level.getBlockState(worldPosition).hasProperty(BlockStateProperties.FACING) || side == null)) {
			if (cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && (side == null || side.getOpposite() == level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING))) {
				return upgrade.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		upgrade.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
		}
	}

	public static enum PBLState {
		ADJ,
		DIAG,
		SOLVED
	}

	//only fitting that I'd use an enum for the entropic enumerator
	public static enum Move {
		U("U", new Vector3d(0, 1, 0), -0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),
		U_("U'", new Vector3d(0, 1, 0), 0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),
		U2("U2", new Vector3d(0, 1, 0), 1.0, 6, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),
		D("D", new Vector3d(0, 1, 0), 0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		D_("D'", new Vector3d(0, 1, 0), -0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		D2("D2", new Vector3d(0, 1, 0), 1.0, 6, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),

		L("L", new Vector3d(1, 0, 0), -0.5, 4, new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(1, -1, 1)),
		L_("L'", new Vector3d(1, 0, 0), 0.5, 4, new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(1, -1, 1)),
		L2("L2", new Vector3d(1, 0, 0), 1.0, 6, new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(1, -1, 1)),
		R("R", new Vector3d(1, 0, 0), 0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(-1, 1, -1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, 1)),
		R_("R'", new Vector3d(1, 0, 0), -0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(-1, 1, -1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, 1)),
		R2("R2", new Vector3d(1, 0, 0), 1.0, 6, new Vector3i(-1, -1, -1), new Vector3i(-1, 1, -1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, 1)),

		F("F", new Vector3d(0, 0, 1), 0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(-1, 1, -1)),
		F_("F'", new Vector3d(0, 0, 1), -0.5, 4, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(-1, 1, -1)),
		F2("F2", new Vector3d(0, 0, 1), 1.0, 6, new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, 1, -1), new Vector3i(-1, 1, -1)),
		B("B", new Vector3d(0, 0, 1), -0.5, 4, new Vector3i(-1, -1, 1), new Vector3i(1, -1, 1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),
		B_("B'", new Vector3d(0, 0, 1), 0.5, 4, new Vector3i(-1, -1, 1), new Vector3i(1, -1, 1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),
		B2("B2", new Vector3d(0, 0, 1), 1.0, 6, new Vector3i(-1, -1, 1), new Vector3i(1, -1, 1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1)),

		X("X", new Vector3d(1, 0, 0), 0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		X_("X'", new Vector3d(1, 0, 0), -0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		X2("X2", new Vector3d(1, 0, 0), 1.0, 6, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Y("Y", new Vector3d(0, 1, 0), 0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Y_("Y'", new Vector3d(0, 1, 0), -0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Y2("Y2", new Vector3d(0, 1, 0), 1.0, 6, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Z("Z", new Vector3d(0, 0, 1), 0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Z_("Z'", new Vector3d(0, 0, 1), -0.5, 4, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1)),
		Z2("Z2", new Vector3d(0, 0, 1), 1.0, 6, new Vector3i(-1, 1, -1), new Vector3i(1, 1, -1), new Vector3i(1, 1, 1), new Vector3i(-1, 1, 1), new Vector3i(-1, -1, -1), new Vector3i(1, -1, -1), new Vector3i(1, -1, 1), new Vector3i(-1, -1, 1));

		public static final Move[] axisMoves = {
				X, X_, Y, Y_, Z, Z_
		};
		public static final Move[] quarterMoves = {
				U, U_, D, D_, L, L_, R, R_, F, F_, B, B_
		};
		public static final Move[] movesX = {
				U, U_, D, D_, F, F_, B, B_
		};
		public static final Move[] movesY = {
				L, L_, R, R_, F, F_, B, B_
		};
		public static final Move[] movesZ = {
				U, U_, D, D_, L, L_, R, R_
		};
		public static final Move[] halfMoves = {
				U2, D2, L2, R2, F2, B2
		};
		public static final Move[] movesX2 = {
				U2, D2, F2, B2
		};
		public static final Move[] movesY2 = {
				L2, R2, F2, B2
		};
		public static final Move[] movesZ2 = {
				U2, D2, L2, R2
		};

		public final String name;
		public final Vector3d axis;
		public final double angle;
		public final int length;
		public final Vector3i[] pieces;

		private Move(String name, Vector3d axis, double angle, int length, Vector3i... pieces) {
			this.name = name;
			this.axis = axis;
			this.angle = angle * Math.PI;
			this.length = length;
			this.pieces = pieces;
		}

		public static void makeMoves(Cubie[][][] cubies, Move[] moves) {
			for (Move move : moves) {
				move.makeMove(cubies);
			}
		}

		public void makeMove(Cubie[][][] cubies) {
			ArrayList<Cubie> affectedCubies = new ArrayList<Cubie>();
			for (Vector3i pos : pieces) {
				for (int x = 0; x < 2; x++) {
					for (int y = 0; y < 2; y++) {
						for (int z = 0; z < 2; z++) {
							Vector3d position = cubies[x][y][z].getPos();
							if (((int) position.x) == pos.x && ((int) position.y) == pos.y && ((int) position.z) == pos.z) {
								affectedCubies.add(cubies[x][y][z]);
							}
						}
					}
				}
			}
			for (Cubie cubie : affectedCubies) {
				cubie.setChanged();
				cubie.rotation.rotateAxis(angle, cubie.rotation.transformInverse(axis, new Vector3d()));
			}
		}

		public Quaternionf makePartialMove(Quaternionf cubie, Vector3d position, float moveAmount) {
			for (Vector3i pos : pieces) {
				if (((int) position.x) == pos.x && ((int) position.y) == pos.y && ((int) position.z) == pos.z) {
					Vector3d newAxis = cubie.transformInverse(axis, new Vector3d());
					cubie.rotateAxis(((float) angle) * moveAmount, (float) newAxis.x(), (float) newAxis.y(), (float) newAxis.z());
				}
			}
			return cubie;
		}

		public Move getOpposite() {
			switch (this) {
			case U:
				return U_;
			case U_:
				return U;
			case D:
				return D_;
			case D_:
				return D;
			case L:
				return L_;
			case L_:
				return L;
			case R:
				return R_;
			case R_:
				return R;
			case F:
				return F_;
			case F_:
				return F;
			case B:
				return B_;
			case B_:
				return B;
			default:
				return U;
			}
		}

		public static Move[] getNextMoves(Move move) {
			if (move == null)
				return quarterMoves;
			switch (move) {
			case L, L_, R, R_:
				return movesX;
			case U, U_, D, D_:
				return movesY;
			case F, F_, B, B_:
				return movesZ;
			case L2, R2:
				return movesX2;
			case U2, D2:
				return movesY2;
			case F2, B2:
				return movesZ2;
			default:
				return quarterMoves;
			}
		}

		public static Move[] getMoves(String algorithm) {
			String[] names = algorithm.split(" ");
			Move[] moves = new Move[names.length];
			for (int i = 0; i < names.length; i++) {
				moves[i] = Move.valueOf(names[i]);
			}
			return moves;
		}
	}

	public static class Cubie {

		public static Vector3d zero = new Vector3d();
		public Vector3d basePosition;
		public Vector3d baseColors;
		public Quaterniond rotation;
		public Vector3d currentPosition = null;
		public Vector3d currentColors = null;

		public Cubie(Vector3d position, Vector3d colors, Quaterniond rotation) {
			this.basePosition = position;
			this.baseColors = colors;
			this.rotation = rotation;
		}

		public Cubie(Vector3d position, Vector3d colors) {
			this(position, colors, new Quaterniond());
		}

		public Cubie(Vector3d position, Quaterniond rotation) {
			this(position, zero, rotation);
		}

		public void setChanged() {
			currentPosition = null;
			currentColors = null;
		}

		public Vector3d getPos() {
			if (currentPosition != null)
				return currentPosition;
			currentPosition = rotation.transform(basePosition, new Vector3d()).round();
			return currentPosition;
		}

		public Vector3d getColors() {
			if (currentColors != null)
				return currentColors;
			currentColors = rotation.transform(baseColors, new Vector3d()).absolute().round();
			return currentColors;
		}
	}
}
