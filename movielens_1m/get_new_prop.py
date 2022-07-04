#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jun  3 10:53:48 2022

@author: giuse
"""

entities = {}

fin = open('mapping_entities.tsv', 'r')
lines = fin.readlines()
fin.close()

for line in lines:
    uri = line.split('\t')[0].strip()
    id = line.split('\t')[1].strip()
    entities[uri] = id

relations = {}

fin = open('mapping_relations.tsv', 'r')
lines = fin.readlines()
fin.close()

for line in lines:
    rel = line.split('\t')[0].strip()
    id = line.split('\t')[1].strip()
    relations[rel] = id


fin = open('new_prop_tab.tsv', 'r')
lines = fin.readlines()
fin.close()

prop_id = 26865
new_ent = {}

fout = open('add_item_prop.tsv', 'w')

for line in lines:    
   
    item = line.split('\t')[1].strip()
    relation = line.split('\t')[2].strip()
    prop = line.split('\t')[3].strip()
    
    if prop not in entities:
        entities[prop]=prop_id
        new_ent[prop]=prop_id
        prop_id += 1

    id1 = entities[item]
    id2 = relations[relation]
    id3 = entities[prop]
    
    fout.write(str(id1)+'\t'+str(id2)+'\t'+str(id3)+'\n')
    
fout.flush()
fout.close()

fout = open('new_ent.tsv', 'w')
for prop in new_ent:
    fout.write(str(new_ent[prop])+'\t'+str(prop)+'\n')
fout.flush()
fout.close()






