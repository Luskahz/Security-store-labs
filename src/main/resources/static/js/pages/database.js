import {api} from '../api.js';
import {adminPage,escapeHtml,message} from '../auth.js';

const PAGE_SIZE=50;
let selectedTable=null;
let offset=0;

function renderShell(){
  document.querySelector('#content').innerHTML=`
    <section class="card">
      <div class="row"><label for="table-select">Tabela</label><select id="table-select"><option value="">Carregando tabelas…</option></select><button id="refresh-tables" class="secondary" type="button">Atualizar</button></div>
      <p class="muted">Visualização somente leitura dos dados reais do banco. Pode incluir dados pessoais e hashes. Até ${PAGE_SIZE} registros por página.</p>
    </section>
    <section id="table-details" class="card"><p class="muted">Selecione uma tabela para visualizar as colunas e os registros.</p></section>
    <div id="pager" class="row"></div>`;
  document.querySelector('#table-select').onchange=event=>{selectedTable=event.target.value||null;offset=0;loadTable()};
  document.querySelector('#refresh-tables').onclick=loadTables;
}

async function loadTables(){
  try{
    const tables=await api('/admin/database/tables');
    const select=document.querySelector('#table-select');
    select.innerHTML='<option value="">Selecione uma tabela</option>'+tables.map(table=>`<option value="${escapeHtml(table.name)}">${escapeHtml(table.name)} · ${escapeHtml(table.type)}</option>`).join('');
    if(selectedTable&&tables.some(table=>table.name===selectedTable)){select.value=selectedTable;await loadTable()}
    else{selectedTable=null;document.querySelector('#table-details').innerHTML='<p class="muted">Selecione uma tabela para visualizar as colunas e os registros.</p>';document.querySelector('#pager').innerHTML=''}
    message(`${tables.length} tabela(s)/view(s) encontrada(s).`);
  }catch(error){message(error.message,true)}
}

function displayValue(value){
  if(value===null||value===undefined)return '<span class="muted">NULL</span>';
  const text=typeof value==='object'?JSON.stringify(value):String(value);
  return escapeHtml(text);
}

async function loadTable(){
  if(!selectedTable)return;
  const details=document.querySelector('#table-details');
  details.innerHTML='<p class="muted">Carregando registros…</p>';
  try{
    const data=await api(`/admin/database/tables/${encodeURIComponent(selectedTable)}?offset=${offset}&limit=${PAGE_SIZE}`);
    const columns=data.columns;
    details.innerHTML=`<div class="row"><h2>${escapeHtml(data.name)}</h2><span class="pill">${escapeHtml(data.type)}</span><span class="muted">${data.rows.length} registro(s) nesta página · posição ${data.offset+1}–${data.offset+data.rows.length}</span></div>
      <h3>Estrutura</h3><div class="table-scroll"><table><thead><tr><th>Coluna</th><th>Tipo</th><th>Nulo</th><th>Chave</th></tr></thead><tbody>${columns.map(column=>`<tr><td>${escapeHtml(column.name)}</td><td>${escapeHtml(column.type)}</td><td>${column.nullable?'Sim':'Não'}</td><td>${column.primaryKey?'PK':'—'}</td></tr>`).join('')}</tbody></table></div>
      <h3>Registros</h3><div class="table-scroll"><table><thead><tr>${columns.map(column=>`<th>${escapeHtml(column.name)}</th>`).join('')}</tr></thead><tbody>${data.rows.length?data.rows.map(row=>`<tr>${columns.map(column=>`<td>${displayValue(row[column.name])}</td>`).join('')}</tr>`).join(''):`<tr><td colspan="${Math.max(columns.length,1)}" class="muted">Tabela sem registros.</td></tr>`}</tbody></table></div>`;
    renderPager(data);
    message(`Exibindo ${data.name}.`);
  }catch(error){details.innerHTML=`<p class="error">${escapeHtml(error.message)}</p>`;document.querySelector('#pager').innerHTML='';message(error.message,true)}
}

function renderPager(data){
  const pager=document.querySelector('#pager');
  pager.innerHTML=`<button id="previous-page" class="secondary" type="button" ${data.offset===0?'disabled':''}>Anterior</button><span class="muted">Página ${Math.floor(data.offset/PAGE_SIZE)+1}</span><button id="next-page" class="secondary" type="button" ${data.hasMore?'':'disabled'}>Próxima</button>`;
  document.querySelector('#previous-page').onclick=()=>{offset=Math.max(0,offset-PAGE_SIZE);loadTable()};
  document.querySelector('#next-page').onclick=()=>{offset+=PAGE_SIZE;loadTable()};
}

try{
  const me=await adminPage('Banco de dados');
  if(!me.permissions.includes('DATABASE_INSPECT_READ'))throw new Error('Seu usuário não tem permissão para inspecionar o banco.');
  renderShell();
  await loadTables();
}catch(error){message(error.message,true)}

