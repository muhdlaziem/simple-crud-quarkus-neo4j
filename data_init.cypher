// type
MATCH(n) DETACH DELETE n;
UNWIND [
  {type: 'Grass'},
  {type: 'Fire'},
  {type: 'Water'},
  {type: 'Dark'},
  {type: 'Fairy'},
  {type: 'Fighting'},
  {type: 'Poison'},
  {type: 'Flying'},
  {type: 'Ground'},
  {type: 'Steel'},
  {type: 'Psychic'},
  {type:'Ghost'},
  {type: 'Rock'},
  {type: 'Bug'},
  {type: 'Ice'}
] AS row
CREATE (:Type {name: row.type});

// origin
UNWIND [
  {origin: 'Kanto'},
  {origin: 'Johto'},
  {origin: 'Hoenn'},
  {origin: 'Sinnoh'},
  {origin: 'Unova'},
  {origin: 'Kalos'},
  {origin: 'Alola'},
  {origin: 'Galar'}
] AS row
CREATE (:Origin {name: row.origin});

// pokemon
UNWIND [
  { name: 'Venusaur', origin: 'Kanto', type: [
    {type:'Grass'},
    {type: 'Poison'}
  ]},
  { name: 'Charizard', origin: 'Kanto', type: [
    {type:'Fire'},
    {type:'Flying'}
  ]},
  { name: 'Blastoise', origin: 'Kanto', type: [
    {type:'Water'}
  ]},
  { name: 'Meganium', origin: 'Johto', type: [
    {type:'Grass'}
  ]},
  { name: 'Typhlosion', origin: 'Johto', type: [
    {type:'Fire'}
  ]},
  { name: 'Feraligatr', origin: 'Johto', type: [
  {type:'Water'}
  ]},
  { name: 'Sceptile', origin: 'Hoenn', type: [
    {type:'Grass'}
  ]},
  { name: 'Blaziken', origin: 'Hoenn', type: [
    {type:'Fire'},
    {type:'Fighting'}
  ]},
  { name: 'Swampert', origin: 'Hoenn', type: [
    {type:'Water'},
    {type:'Ground'}
  ]},
  { name: 'Torterra', origin: 'Sinnoh', type: [
    {type:'Grass'},
    {type:'Ground'}
  ]},
  { name: 'Infernape', origin: 'Sinnoh', type: [
    {type:'Fire'},
    {type:'Fighting'}
  ]},
  { name: 'Empoleon', origin: 'Sinnoh', type: [
    {type:'Water'},
    {type:'Steel'}
  ]},
  { name: 'Serperior', origin: 'Unova', type: [
    {type:'Grass'}
  ]},
  { name: 'Emboar', origin: 'Unova', type: [
    {type:'Fire'},
    {type:'Fighting'}
  ]},
  { name: 'Samurott', origin: 'Unova', type: [
    {type:'Water'}
  ]},
  { name: 'Chesnaught', origin: 'Kalos', type: [
    {type:'Grass'},
    {type:'Fighting'}
  ]},
  { name: 'Delphox', origin: 'Kalos', type: [
    {type:'Fire'},
    {type:'Psychic'}
  ]},
  { name: 'Greninja', origin: 'Kalos', type: [
    {type:'Water'},
    {type:'Dark'}
  ]},
  { name: 'Decidueye', origin: 'Alola', type: [
    {type:'Water'},
    {type:'Ghost'}
  ]},
  { name: 'Incineroar', origin: 'Alola', type: [
    {type:'Fire'},
    {type:'Dark'}
  ]},
  { name: 'Primarina', origin: 'Alola', type: [
    {type:'Water'},
    {type:'Fairy'}
  ]},
  { name: 'Rillaboom', origin: 'Galar', type: [
    {type:'Grass'}
  ]},
  { name: 'Cinderace', origin: 'Galar', type: [
    {type:'Fire'}
  ]},
  { name: 'Inteleon', origin: 'Galar', type: [
    {type:'Water'}
  ]}

] AS row
CREATE (p: Pokemon {name: row.name})
WITH p, row
MATCH (c: Origin { name: row.origin})
MERGE (p)-[:IS_FROM]->(c)
WITH p, row
UNWIND row.type AS type
MATCH (t: Type {name: type.type})
MERGE (p)-[:IS_TYPE]->(t);

// For Employee Example
CREATE (Laziem: Employee {name:'Laziem', title: 'Junior AI Engineer', started_in: 2020})
CREATE (Hazim: Employee {name:'Hazim', title: 'AI Software Engineer', started_in: 2020});

// Add some constraints
CREATE CONSTRAINT ON (node:Origin) ASSERT (node.name) IS UNIQUE;
CREATE CONSTRAINT ON (node:Pokemon) ASSERT (node.name) IS UNIQUE;
CREATE CONSTRAINT ON (node:Type) ASSERT (node.type) IS UNIQUE;
