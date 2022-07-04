package kale.joint;

import kale.struct.Matrix;
import kale.struct.Rule;

public class RuleGradient {
	public Rule Rule;
	public Rule NegRule;
	public Matrix MatrixE;
	public Matrix MatrixR;
	public Matrix MatrixEGradient;
	public Matrix MatrixRGradient;
	double dDelta;
	double dFstPi;
	double dSndPi;
	double dTrdPi;
	double dFouPi;
	double dNegFstPi;
	double dNegSndPi;
	double dNegTrdPi;
	double dNegFouPi;
	double costrule;
	double Negcostrule;
	
	public RuleGradient(
			Rule inRule,
			Rule inNegRule,
			Matrix inMatrixE, 
			Matrix inMatrixR,
			Matrix inMatrixEGradient, 
			Matrix inMatrixRGradient,
			double inDelta) {
		Rule = inRule;
		NegRule = inNegRule;
		MatrixE = inMatrixE;
		MatrixR = inMatrixR;
		MatrixEGradient = inMatrixEGradient;
		MatrixRGradient = inMatrixRGradient;
		dDelta = inDelta;
	}
	
	public void calculateGradient(double weight) throws Exception {
		int iNumberOfFactors = MatrixE.columns();
		int iFstHead = Rule.fstTriple().head();
		int iFstTail = Rule.fstTriple().tail();
		int iFstRelation = Rule.fstTriple().relation();
		int iSndHead = Rule.sndTriple().head();
		int iSndTail = Rule.sndTriple().tail();
		int iSndRelation = Rule.sndTriple().relation();
		
		int iNegFstHead = NegRule.fstTriple().head();
		int iNegFstTail = NegRule.fstTriple().tail();
		int iNegFstRelation = NegRule.fstTriple().relation();
		int iNegSndHead = NegRule.sndTriple().head();
		int iNegSndTail = NegRule.sndTriple().tail();
		int iNegSndRelation = NegRule.sndTriple().relation();
		
		double dValue = 1.0 / (3.0 * Math.sqrt(iNumberOfFactors));
		
		if(Rule.trdTriple()==null){
			dFstPi = 0.0;
			double dPosPi =0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi -= Math.abs(MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p));
			}
			dFstPi *= dValue;
			dFstPi += 1.0;
			
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi -= Math.abs(MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p));
			}
			dSndPi *= dValue;
			dSndPi += 1.0;
			dPosPi = dFstPi *(dSndPi- 1.0 ) + 1.0;
			
			dNegFstPi = 0.0;
			double dNegPi=0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegFstPi -= Math.abs(MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p));
			}
			dNegFstPi *= dValue;
			dNegFstPi += 1.0;
			
			dNegSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegSndPi -= Math.abs(MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p));
			}
			dNegSndPi *= dValue;
			dNegSndPi += 1.0;
			dNegPi = dNegFstPi *(dNegSndPi- 1.0 ) + 1.0;

			if (dDelta - dPosPi + dNegPi > 0.0) {
				for (int p = 0; p < iNumberOfFactors; p++) {
					double dFstSgn = 0.0;
					if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) > 0) {
						dFstSgn = 1.0;
					} else if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) < 0) {
						dFstSgn = -1.0;
					}
					MatrixEGradient.add(iFstHead, p, weight * (dSndPi-1) * dValue * dFstSgn);
					MatrixEGradient.add(iFstTail, p, -1.0 * weight * (dSndPi-1) * dValue * dFstSgn);
					MatrixRGradient.add(iFstRelation, p, weight * (dSndPi-1) * dValue * dFstSgn);
					
					double dSndSgn = 0.0;
					if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) > 0) {
						dSndSgn = 1.0;
					} else if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) < 0) {
						dSndSgn = -1.0;
					}
					MatrixEGradient.add(iSndHead, p, weight * dFstPi * dValue * dSndSgn);
					MatrixEGradient.add(iSndTail, p, -1.0 * weight * dFstPi * dValue * dSndSgn);
					MatrixRGradient.add(iSndRelation, p, weight * dFstPi * dValue * dSndSgn);
					
					double dNegFstSgn = 0.0;
					if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) > 0) {
						dNegFstSgn = 1.0;
					} else if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) < 0) {
						dNegFstSgn = -1.0;
					}
					MatrixEGradient.add(iNegFstHead, p,  -1.0 * weight * (dNegSndPi-1) * dValue * dNegFstSgn);
					MatrixEGradient.add(iNegFstTail, p, weight * (dNegSndPi-1) * dValue * dNegFstSgn);
					MatrixRGradient.add(iNegFstRelation, p,  -1.0 * weight * (dNegSndPi-1) * dValue * dNegFstSgn);
											
					double dNegSndSgn = 0.0;
					if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) > 0) {
						dNegSndSgn = 1.0;
					} else if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) < 0) {
						dNegSndSgn = -1.0;
					}
					MatrixEGradient.add(iNegSndHead, p, -1.0 * weight * dNegFstPi * dValue * dNegSndSgn);
					MatrixEGradient.add(iNegSndTail, p, weight * dNegFstPi * dValue * dNegSndSgn);
					MatrixRGradient.add(iNegSndRelation, p, -1.0 * weight * dNegFstPi * dValue * dNegSndSgn);

				}
				
			}
		}
//		long rule
		else if (Rule.fouTriple() == null){
			
			//System.out.println("Tre");
			
			int iTrdHead = Rule.trdTriple().head();
			int iTrdTail = Rule.trdTriple().tail();
			int iTrdRelation = Rule.trdTriple().relation();
			
			int iNegTrdHead = NegRule.trdTriple().head();
			int iNegTrdTail = NegRule.trdTriple().tail();
			int iNegTrdRelation = NegRule.trdTriple().relation();
			
			dFstPi = 0.0;
			double dPosPi =0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi -= Math.abs(MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p));
			}
			dFstPi *= dValue;
			dFstPi += 1.0;
			
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi -= Math.abs(MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p));
			}
			dSndPi *= dValue;
			dSndPi += 1.0;
			
			
			dTrdPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dTrdPi -= Math.abs(MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p));
			}
			dTrdPi *= dValue;
			dTrdPi += 1.0;
			dPosPi = (dFstPi * dSndPi) * (dTrdPi - 1.0 ) + 1.0;
			
			dNegFstPi = 0.0;
			double dNegPi=0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegFstPi -= Math.abs(MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p));
			}
			dNegFstPi *= dValue;
			dNegFstPi += 1.0;
			
			dNegSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegSndPi -= Math.abs(MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p));
			}
			dNegSndPi *= dValue;
			dNegSndPi += 1.0;
			
			dNegTrdPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegTrdPi -= Math.abs(MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p));
			}
			dNegTrdPi *= dValue;
			dNegTrdPi += 1.0;
			
			dNegPi = (dNegFstPi * dNegSndPi) * ( dNegTrdPi - 1.0 ) + 1.0;
			
			if (dDelta - dPosPi + dNegPi > 0.0) {;
				for (int p = 0; p < iNumberOfFactors; p++) {
					double dFstSgn = 0.0;
					if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) > 0) {
						dFstSgn = 1.0;
					} else if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) < 0) {
						dFstSgn = -1.0;
					}
					MatrixEGradient.add(iFstHead, p, weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					MatrixEGradient.add(iFstTail, p, -1.0 * weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					MatrixRGradient.add(iFstRelation, p, weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					
					double dSndSgn = 0.0;
					if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) > 0) {
						dSndSgn = 1.0;
					} else if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) < 0) {
						dSndSgn = -1.0;
					}
					MatrixEGradient.add(iSndHead, p, weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					MatrixEGradient.add(iSndTail, p, -1.0 * weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					MatrixRGradient.add(iSndRelation, p, weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					
					double dTrdSgn = 0.0;
					if (MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p) > 0) {
						dTrdSgn = 1.0;
					} else if (MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p) < 0) {
						dTrdSgn = -1.0;
					}
					MatrixEGradient.add(iTrdHead, p, weight * dFstPi * dSndPi * dValue * dTrdSgn);
					MatrixEGradient.add(iTrdTail, p, -1.0 * weight * dFstPi * dSndPi * dValue * dTrdSgn);
					MatrixRGradient.add(iTrdRelation, p, weight * dFstPi * dSndPi * dValue * dTrdSgn);
					
					double dNegFstSgn = 0.0;
					if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) > 0) {
						dNegFstSgn = 1.0;
					} else if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) < 0) {
						dNegFstSgn = -1.0;
					}
					MatrixEGradient.add(iNegFstHead, p,  -1.0 * weight * dNegSndPi * ( dNegTrdPi - 1.0 ) * dValue * dNegFstSgn);
					MatrixEGradient.add(iNegFstTail, p, weight * dNegSndPi * ( dNegTrdPi - 1.0 ) * dValue * dNegFstSgn);
					MatrixRGradient.add(iNegFstRelation, p,  -1.0 * weight * dNegSndPi * ( dNegTrdPi - 1.0 ) * dValue * dNegFstSgn);
										
					double dNegSndSgn = 0.0;
					if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) > 0) {
						dNegSndSgn = 1.0;
					} else if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) < 0) {
						dNegSndSgn = -1.0;
					}
					MatrixEGradient.add(iNegSndHead, p, -1.0 * weight * dNegFstPi * ( dNegTrdPi - 1.0 ) * dValue * dNegSndSgn);
					MatrixEGradient.add(iNegSndTail, p, weight * dNegFstPi * ( dNegTrdPi - 1.0 ) * dValue * dNegSndSgn);
					MatrixRGradient.add(iNegSndRelation, p, -1.0 * weight * dNegFstPi * ( dNegTrdPi - 1.0 ) * dValue * dNegSndSgn);

					double dNegTrdSgn = 0.0;
					if (MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p) > 0) {
						dNegTrdSgn = 1.0;
					} else if (MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p) < 0) {
						dNegTrdSgn = -1.0;
					}
					MatrixEGradient.add(iNegTrdHead, p, -1.0 * weight * dNegFstPi * dNegSndPi * dValue * dNegTrdSgn);
					MatrixEGradient.add(iNegTrdTail, p, weight * dNegFstPi * dNegSndPi * dValue * dNegTrdSgn);
					MatrixRGradient.add(iNegTrdRelation, p, -1.0 * weight * dNegFstPi * dNegSndPi * dValue * dNegTrdSgn);

				}
				
			}
			
		} else {
			
			//System.out.println("Quattro");
			
			int iTrdHead = Rule.trdTriple().head();
			int iTrdTail = Rule.trdTriple().tail();
			int iTrdRelation = Rule.trdTriple().relation();			
			int iNegTrdHead = NegRule.trdTriple().head();
			int iNegTrdTail = NegRule.trdTriple().tail();
			int iNegTrdRelation = NegRule.trdTriple().relation();
			
			// quarta tripla
			int iFouHead = Rule.fouTriple().head();
			int iFouTail = Rule.fouTriple().tail();
			int iFouRelation = Rule.fouTriple().relation();			
			int iNegFouHead = NegRule.fouTriple().head();
			int iNegFouTail = NegRule.fouTriple().tail();
			int iNegFouRelation = NegRule.fouTriple().relation();
			
			// ok
			dFstPi = 0.0;
			double dPosPi =0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFstPi -= Math.abs(MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p));
			}
			dFstPi *= dValue;
			dFstPi += 1.0;
			
			// ok
			dSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dSndPi -= Math.abs(MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p));
			}
			dSndPi *= dValue;
			dSndPi += 1.0;
			
			// ok 
			dTrdPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dTrdPi -= Math.abs(MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p));
			}
			dTrdPi *= dValue;
			dTrdPi += 1.0;
			
			// da aggiungere la quarta tripla -- done
			dFouPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dFouPi -= Math.abs(MatrixE.get(iFouHead, p) + MatrixR.get(iFouRelation, p) - MatrixE.get(iFouTail, p));
			}
			dFouPi *= dValue;
			dFouPi += 1.0;
			
			// da cambiare con quarta triple (credo (1*2*3) *(4-c)+c) -- done
			dPosPi = (dFstPi * dSndPi * dTrdPi) * (dFouPi - 1.0 ) + 1.0;
			
			// ok
			dNegFstPi = 0.0;
			double dNegPi=0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegFstPi -= Math.abs(MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p));
			}
			dNegFstPi *= dValue;
			dNegFstPi += 1.0;
			
			// ok
			dNegSndPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegSndPi -= Math.abs(MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p));
			}
			dNegSndPi *= dValue;
			dNegSndPi += 1.0;
			
			// ok
			dNegTrdPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegTrdPi -= Math.abs(MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p));
			}
			dNegTrdPi *= dValue;
			dNegTrdPi += 1.0;
			
			// da aggiungere quarta triple -- done
			dNegFouPi = 0.0;
			for (int p = 0; p < iNumberOfFactors; p++) {
				dNegFouPi -= Math.abs(MatrixE.get(iNegFouHead, p) + MatrixR.get(iNegFouRelation, p) - MatrixE.get(iNegFouTail, p));
			}
			dNegFouPi *= dValue;
			dNegFouPi += 1.0;
			
			// da cambiare con quarta triple (1*2*3) * (4-c) + c) -- done
			dNegPi = (dNegFstPi * dNegSndPi * dNegTrdPi) * ( dNegFouPi - 1.0 ) + 1.0;
			
			if (dDelta - dPosPi + dNegPi > 0.0) {;
				for (int p = 0; p < iNumberOfFactors; p++) {
					
					// da controllare - ok
					double dFstSgn = 0.0;
					if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) > 0) {
						dFstSgn = 1.0;
					} else if (MatrixE.get(iFstHead, p) + MatrixR.get(iFstRelation, p) - MatrixE.get(iFstTail, p) < 0) {
						dFstSgn = -1.0;
					}
					//MatrixEGradient.add(iFstHead, p, weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					//MatrixEGradient.add(iFstTail, p, -1.0 * weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					//MatrixRGradient.add(iFstRelation, p, weight * dSndPi * (dTrdPi-1) * dValue * dFstSgn);
					MatrixEGradient.add(iFstHead, p, weight * dSndPi * dTrdPi * (dFouPi-1) * dValue * dFstSgn);
					MatrixEGradient.add(iFstTail, p, -1.0 * weight * dSndPi * dTrdPi * (dFouPi-1) * dValue * dFstSgn);
					MatrixRGradient.add(iFstRelation, p, weight * dSndPi * dTrdPi * (dFouPi-1) * dValue * dFstSgn);
					
					
					// da controllare
					double dSndSgn = 0.0;
					if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) > 0) {
						dSndSgn = 1.0;
					} else if (MatrixE.get(iSndHead, p) + MatrixR.get(iSndRelation, p) - MatrixE.get(iSndTail, p) < 0) {
						dSndSgn = -1.0;
					}
					//MatrixEGradient.add(iSndHead, p, weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					//MatrixEGradient.add(iSndTail, p, -1.0 * weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					//MatrixRGradient.add(iSndRelation, p, weight * dFstPi * (dTrdPi-1) * dValue * dSndSgn);
					MatrixEGradient.add(iSndHead, p, weight * dFstPi * dTrdPi * (dFouPi-1) * dValue * dSndSgn);
					MatrixEGradient.add(iSndTail, p, -1.0 * weight * dFstPi * dTrdPi * (dFouPi-1) * dValue * dSndSgn);
					MatrixRGradient.add(iSndRelation, p, weight * dFstPi * dTrdPi * (dFouPi-1) * dValue * dSndSgn);
					
					// da controllare - ok
					double dTrdSgn = 0.0;
					if (MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p) > 0) {
						dTrdSgn = 1.0;
					} else if (MatrixE.get(iTrdHead, p) + MatrixR.get(iTrdRelation, p) - MatrixE.get(iTrdTail, p) < 0) {
						dTrdSgn = -1.0;
					}
					//MatrixEGradient.add(iTrdHead, p, weight * dFstPi * dSndPi * dValue * dTrdSgn);
					//MatrixEGradient.add(iTrdTail, p, -1.0 * weight * dFstPi * dSndPi * dValue * dTrdSgn);
					//MatrixRGradient.add(iTrdRelation, p, weight * dFstPi * dSndPi * dValue * dTrdSgn);
					MatrixEGradient.add(iTrdHead, p, weight * dFstPi * dSndPi * (dFouPi-1) * dValue * dTrdSgn);
					MatrixEGradient.add(iTrdTail, p, -1.0 * weight * dFstPi * dSndPi * (dFouPi-1) * dValue * dTrdSgn);
					MatrixRGradient.add(iTrdRelation, p, weight * dFstPi * dSndPi * (dFouPi-1) * dValue * dTrdSgn);
					
					// da controllare - ok 
					double dFouSgn = 0.0;
					if (MatrixE.get(iFouHead, p) + MatrixR.get(iFouRelation, p) - MatrixE.get(iFouTail, p) > 0) {
						dFouSgn = 1.0;
					} else if (MatrixE.get(iFouHead, p) + MatrixR.get(iFouRelation, p) - MatrixE.get(iFouTail, p) < 0) {
						dFouSgn = -1.0;
					}
					MatrixEGradient.add(iFouHead, p, weight * dFstPi * dSndPi * dTrdPi * dValue * dFouSgn);
					MatrixEGradient.add(iFouTail, p, -1.0 * weight * dFstPi * dSndPi * dTrdPi * dValue * dFouSgn);
					MatrixRGradient.add(iFouRelation, p, weight * dFstPi * dSndPi * dTrdPi * dValue * dFouSgn);
					
					
					double dNegFstSgn = 0.0;
					if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) > 0) {
						dNegFstSgn = 1.0;
					} else if (MatrixE.get(iNegFstHead, p) + MatrixR.get(iNegFstRelation, p) - MatrixE.get(iNegFstTail, p) < 0) {
						dNegFstSgn = -1.0;
					}
					MatrixEGradient.add(iNegFstHead, p,  -1.0 * weight * dNegSndPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegFstSgn);
					MatrixEGradient.add(iNegFstTail, p, weight * dNegSndPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegFstSgn);
					MatrixRGradient.add(iNegFstRelation, p,  -1.0 * weight * dNegSndPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegFstSgn);
										
					double dNegSndSgn = 0.0;
					if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) > 0) {
						dNegSndSgn = 1.0;
					} else if (MatrixE.get(iNegSndHead, p) + MatrixR.get(iNegSndRelation, p) - MatrixE.get(iNegSndTail, p) < 0) {
						dNegSndSgn = -1.0;
					}
					MatrixEGradient.add(iNegSndHead, p, -1.0 * weight * dNegFstPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegSndSgn);
					MatrixEGradient.add(iNegSndTail, p, weight * dNegFstPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegSndSgn);
					MatrixRGradient.add(iNegSndRelation, p, -1.0 * weight * dNegFstPi * dNegTrdPi * ( dNegFouPi - 1.0 ) * dValue * dNegSndSgn);

					double dNegTrdSgn = 0.0;
					if (MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p) > 0) {
						dNegTrdSgn = 1.0;
					} else if (MatrixE.get(iNegTrdHead, p) + MatrixR.get(iNegTrdRelation, p) - MatrixE.get(iNegTrdTail, p) < 0) {
						dNegTrdSgn = -1.0;
					}
					MatrixEGradient.add(iNegTrdHead, p, -1.0 * weight * dNegFstPi * dNegSndPi * ( dNegFouPi - 1.0 ) * dValue * dNegTrdSgn);
					MatrixEGradient.add(iNegTrdTail, p, weight * dNegFstPi * dNegSndPi * ( dNegFouPi - 1.0 ) * dValue * dNegTrdSgn);
					MatrixRGradient.add(iNegTrdRelation, p, -1.0 * weight * dNegFstPi * dNegSndPi * ( dNegFouPi - 1.0 ) * dValue * dNegTrdSgn);

					
					double dNegFouSgn = 0.0;
					if (MatrixE.get(iNegFouHead, p) + MatrixR.get(iNegFouRelation, p) - MatrixE.get(iNegFouTail, p) > 0) {
						dNegFouSgn = 1.0;
					} else if (MatrixE.get(iNegFouHead, p) + MatrixR.get(iNegFouRelation, p) - MatrixE.get(iNegFouTail, p) < 0) {
						dNegFouSgn = -1.0;
					}
					MatrixEGradient.add(iNegFouHead, p, -1.0 * weight * dNegFstPi * dNegSndPi * dNegTrdPi * dValue * dNegFouSgn);
					MatrixEGradient.add(iNegFouTail, p, weight * dNegFstPi * dNegSndPi * dNegTrdPi * dValue * dNegFouSgn);
					MatrixRGradient.add(iNegFouRelation, p, -1.0 * weight * dNegFstPi * dNegSndPi * dNegTrdPi *  dValue * dNegFouSgn);

				}
				
			}
		}
	}	
}
