from pdb import set_trace as bp
import json
import copy
import numpy as np
from pprint import pprint

ALGO = 100 # no sabia ue poner,lo usa el for anidado de la resta

def main():

	#Extraigo datos del json
	with open('../examples_rdp/tpfinal_config_salidaDividida.json') as data_file:
		data = json.load(data_file)

	matrixInvariantT = copy.deepcopy(data['matrixInvariantT'])
	vectorMark       = copy.deepcopy(data['vectorMark'])
	matrixI          = copy.deepcopy(data['matrixI'])

	#convierto matrices a numpy array para trabajar mas comodo

	matrixInvariantT = np.array(matrixInvariantT)
	vectorMark       = np.array(vectorMark)
	matrixI          = np.array(matrixI)

	n_cols_matrixI = np.shape(matrixI)[1]
	n_rows_matrixI = np.shape(matrixI)[0]

	n_cols_matrixInvT = np.shape(matrixInvariantT)[1]
	n_rows_matrixInvT = np.shape(matrixInvariantT)[0]

	#creo vector de ceros
	vDisp = [0]*n_cols_matrixI

	#leo el vector de  y lo guardo en una matriz
	#de n filas x 2 columnas
	with open('vdis.txt') as disp_file:
		vectDisparos = []
		for line in disp_file: # read rest of lines
			vectDisparos.append([int(x) for x in line.split()])

	#le resto 1 a cada posicion
	for row in vectDisparos:
		row[1] -= 1
		0
	#acomodo los datos
	for row in vectDisparos :
		vDisp[row[1]] = row[0]

	#restas
	vDisp = np.array(vDisp)
	v_aux = np.array(([0]*n_cols_matrixI))

	for i in range(n_rows_matrixInvT):
		for j in range(ALGO):
			v_aux = vDisp - matrixInvariantT[i]
			if len(filter(lambda x : x < 0, v_aux)) > 0 : #busco si hay agun negativo
				break
			else:
				vDisp = v_aux

	#transponer
	transposed_vDisp = np.array(vDisp).reshape(len(vDisp),1)
	transposed_vectorMark = np.array(vectorMark).reshape(len(vectorMark),1)
	#multiplicacion
	mul = np.matmul(matrixI ,transposed_vDisp)
	result = mul + transposed_vectorMark

	print '\n\n resto : \n\n', vDisp

	print '\n\n marcado final : \n\n',result








if __name__ == '__main__':
    main()