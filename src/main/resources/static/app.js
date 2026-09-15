let authorization='';
document.querySelector('#login').addEventListener('submit',async event=>{event.preventDefault();authorization='Basic '+btoa(`${email.value}:${password.value}`);const response=await fetch('/api/iam/me',{headers:{Authorization:authorization}});identity.textContent=response.ok?`Conectado: ${(await response.json()).name}`:'Credenciais inválidas';});
fetch('/api/products').then(r=>r.json()).then(items=>{products.innerHTML=items.map(p=>`<article class="card"><h3>${escapeHtml(p.name)}</h3><p>${escapeHtml(p.description||'')}</p><strong>R$ ${Number(p.price).toFixed(2)}</strong><br><small>Estoque: ${p.stock}</small></article>`).join('');});
function escapeHtml(value){const node=document.createElement('div');node.textContent=value;return node.innerHTML;}
