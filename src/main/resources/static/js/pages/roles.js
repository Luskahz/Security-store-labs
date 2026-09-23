import {api} from '../api.js';
import {adminPage,escapeHtml,message,has} from '../auth.js';

try {
  const me = await adminPage('Roles');
  if (!has(me,'AUTHORIZATION_ROLE_READ')) throw new Error('Sem permissão para consultar roles');
  document.querySelector('#content').innerHTML = `${has(me,'AUTHORIZATION_ROLE_CREATE')?'<section class="card"><h2>Criar role</h2><form id="create"><label>Nome<input name="name" required></label><label>Descrição<input name="description"></label><button>Criar</button></form></section>':''}<section class="card"><table><thead><tr><th>Nome</th><th>Descrição</th><th>Status</th><th>Usuários</th><th>Permissions</th><th>Ações</th></tr></thead><tbody id="table"></tbody></table></section>`;
  async function load() {
    const roles = await api('/admin/roles');
    const users = has(me,'IDENTITY_USER_READ') ? await api('/admin/users') : null;
    const permissionCounts = await Promise.all(roles.map(async role => (await api(`/admin/roles/${role.id}/permissions`)).length));
    document.querySelector('#table').innerHTML = roles.map((role,index) => `<tr><td><a href="/admin/role.html?id=${role.id}">${escapeHtml(role.name)}</a></td><td>${escapeHtml(role.description)}</td><td>${escapeHtml(role.status)}</td><td>${users?users.filter(user=>user.roles.includes(role.name)).length:'—'}</td><td>${permissionCounts[index]}</td><td>${has(me,'AUTHORIZATION_ROLE_UPDATE')&&role.name!=='ADMIN'?`<button class="secondary" data-id="${role.id}" data-action="${role.status==='ACTIVE'?'disable':'enable'}">${role.status==='ACTIVE'?'Desabilitar':'Habilitar'}</button>`:''} ${has(me,'AUTHORIZATION_ROLE_DELETE')&&role.name!=='ADMIN'?`<button class="danger" data-id="${role.id}" data-action="delete">Excluir</button>`:''}</td></tr>`).join('');
    document.querySelectorAll('[data-action]').forEach(button => button.onclick = async () => {
      try { await api(`/admin/roles/${button.dataset.id}${button.dataset.action==='delete'?'':`/${button.dataset.action}`}`,{method:button.dataset.action==='delete'?'DELETE':'PATCH'}); await load(); }
      catch (error) { message(error.message,true); }
    });
  }
  document.querySelector('#create')?.addEventListener('submit',async event => {
    event.preventDefault();
    try { await api('/admin/roles',{method:'POST',body:JSON.stringify(Object.fromEntries(new FormData(event.target)))}); event.target.reset(); message('Role criada'); await load(); }
    catch (error) { message(error.message,true); }
  });
  await load();
} catch (error) { message(error.message,true); }
