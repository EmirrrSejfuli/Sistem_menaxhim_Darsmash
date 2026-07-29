/**
 * Logjika e planit te uljes: zvarritje (drag & drop) e tavolinave dhe komunikimi
 * me endpoint-et REST /api/seating/tables per te ruajtur ndryshimet ne kohe reale.
 */
(function () {
  const canvas = document.getElementById('seatCanvas');
  if (!canvas) return;

  const hallId = canvas.getAttribute('data-hall-id');
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  function authHeaders(extra) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, extra || {});
    if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
    return headers;
  }

  // ---------- Zvarritje (Drag & Drop) ----------
  let dragEl = null, offX = 0, offY = 0;

  function attachDrag(el) {
    el.addEventListener('mousedown', (e) => {
      dragEl = el;
      const rect = el.getBoundingClientRect();
      offX = e.clientX - rect.left;
      offY = e.clientY - rect.top;
      e.preventDefault();
    });
    el.addEventListener('click', (e) => {
      if (draggedRecently) { draggedRecently = false; return; }
      openEditModal(el);
    });
  }

  let draggedRecently = false;

  document.addEventListener('mousemove', (e) => {
    if (!dragEl) return;
    draggedRecently = true;
    const cRect = canvas.getBoundingClientRect();
    let x = e.clientX - cRect.left - offX;
    let y = e.clientY - cRect.top - offY;
    x = Math.max(0, Math.min(x, cRect.width - dragEl.offsetWidth));
    y = Math.max(0, Math.min(y, cRect.height - dragEl.offsetHeight));
    dragEl.style.left = x + 'px';
    dragEl.style.top = y + 'px';
  });

  document.addEventListener('mouseup', async () => {
    if (dragEl) {
      const id = dragEl.getAttribute('data-id');
      const posX = parseFloat(dragEl.style.left);
      const posY = parseFloat(dragEl.style.top);
      try {
        await fetch('/api/seating/tables/' + id, {
          method: 'PUT',
          headers: authHeaders(),
          body: JSON.stringify({ posX, posY })
        });
      } catch (err) { console.error('Gabim ne ruajtjen e pozicionit', err); }
    }
    dragEl = null;
  });

  canvas.querySelectorAll('.table-el').forEach(attachDrag);

  // ---------- Modal per shtim/perditesim ----------
  const overlay = document.getElementById('overlay');
  const modalTitle = document.getElementById('modalTitle');
  const tName = document.getElementById('tName');
  const tCap = document.getElementById('tCap');
  const tShape = document.getElementById('tShape');
  const tGuests = document.getElementById('tGuests');
  const deleteBtn = document.getElementById('deleteTableBtn');
  const saveBtn = document.getElementById('saveTableBtn');
  const cancelBtn = document.getElementById('cancelModalBtn');
  const addBtn = document.getElementById('addTableBtn');

  let editingEl = null;

  function openModal() { overlay.style.display = 'flex'; }
  function closeModal() { overlay.style.display = 'none'; editingEl = null; }

  function openNewModal() {
    editingEl = null;
    modalTitle.textContent = 'Tavolinë e Re';
    tName.value = 'Tavolina ' + (canvas.querySelectorAll('.table-el').length + 1);
    tCap.value = 8;
    tShape.value = 'RRUMBULLAKET';
    tGuests.value = 0;
    deleteBtn.style.display = 'none';
    openModal();
  }

  function openEditModal(el) {
    editingEl = el;
    modalTitle.textContent = 'Ndrysho Tavolinën';
    tName.value = el.getAttribute('data-name');
    tCap.value = el.getAttribute('data-capacity');
    tShape.value = el.getAttribute('data-shape');
    tGuests.value = el.getAttribute('data-guests');
    deleteBtn.style.display = 'inline-flex';
    openModal();
  }

  addBtn?.addEventListener('click', openNewModal);
  cancelBtn?.addEventListener('click', closeModal);
  overlay?.addEventListener('click', (e) => { if (e.target === overlay) closeModal(); });

  function renderTableElement(dto) {
    const el = document.createElement('div');
    el.className = 'table-el ' + dto.shape + (dto.overCapacity ? ' over' : '');
    el.setAttribute('data-id', dto.id);
    el.setAttribute('data-name', dto.name);
    el.setAttribute('data-capacity', dto.capacity);
    el.setAttribute('data-shape', dto.shape);
    el.setAttribute('data-guests', dto.guests);
    el.style.left = dto.posX + 'px';
    el.style.top = dto.posY + 'px';
    el.innerHTML = '<div class="tn">' + escapeHtml(dto.name) + '</div><div class="tc">' + dto.guests + '/' + dto.capacity + '</div>';
    attachDrag(el);
    return el;
  }

  function escapeHtml(s) {
    return (s || '').toString().replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }

  saveBtn?.addEventListener('click', async () => {
    const payload = {
      hallId: hallId,
      name: tName.value.trim() || 'Tavolinë',
      capacity: parseInt(tCap.value, 10) || 1,
      shape: tShape.value,
      guests: parseInt(tGuests.value, 10) || 0
    };
    try {
      if (editingEl) {
        const id = editingEl.getAttribute('data-id');
        const res = await fetch('/api/seating/tables/' + id, { method: 'PUT', headers: authHeaders(), body: JSON.stringify(payload) });
        const dto = await res.json();
        const newEl = renderTableElement(dto);
        newEl.style.left = editingEl.style.left;
        newEl.style.top = editingEl.style.top;
        editingEl.replaceWith(newEl);
      } else {
        payload.posX = 20 + Math.random() * 60;
        payload.posY = 20 + Math.random() * 60;
        const res = await fetch('/api/seating/tables', { method: 'POST', headers: authHeaders(), body: JSON.stringify(payload) });
        const dto = await res.json();
        const newEl = renderTableElement(dto);
        canvas.appendChild(newEl);
      }
      closeModal();
    } catch (err) {
      console.error('Gabim ne ruajtjen e tavolinës', err);
      alert('Ndodhi një gabim gjatë ruajtjes së tavolinës.');
    }
  });

  deleteBtn?.addEventListener('click', async () => {
    if (!editingEl) return;
    if (!confirm('Të fshihet kjo tavolinë?')) return;
    const id = editingEl.getAttribute('data-id');
    try {
      await fetch('/api/seating/tables/' + id, { method: 'DELETE', headers: authHeaders() });
      editingEl.remove();
      closeModal();
    } catch (err) {
      console.error('Gabim ne fshirjen e tavolinës', err);
    }
  });
})();
