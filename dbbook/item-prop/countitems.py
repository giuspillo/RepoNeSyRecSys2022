fin = open('train.tsv', 'r')
lines = fin.readlines()
fin.close()

items_train = set()

for line in lines:
	item = line.split('\t')[0]
	items_train.add(item)

fin = open('test.tsv', 'r')
lines = fin.readlines()
fin.close()

items_test = set()

for line in lines:
	item = line.split('\t')[1]
	items_test.add(item)
 
fout = open('missing.tsv', 'w')

for item in items_test:
    if item not in items_train:
        fout.write(item+'\n')
        
fout.flush()
fout.close()

print(len(items_test))
print(len(items_train))
print(len(items_test) -len(items_train))