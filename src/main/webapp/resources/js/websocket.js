(function () {
    // Espera a que el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    function init() {
        const url = new URL(window.location.href);
        let idConversacion = url.searchParams.get('idConversacion');
        let idEmisor = url.searchParams.get('user');

        // Preferible inyectar esto desde el servidor (JSF)
        const contextPath = (window.appConfig && window.appConfig.contextPath)
                || ('/' + (location.pathname.split('/')[1] || ''));

        if (window.appConfig) {
            idConversacion = idConversacion || window.appConfig.idConversacion;
            idEmisor = idEmisor || window.appConfig.idEmisor;
        }

        if (!idConversacion || !idEmisor) {
            console.error('Faltan datos para el WebSocket', {idConversacion, idEmisor});
            return;
        }

        const wsProto = (location.protocol === 'https:') ? 'wss:' : 'ws:';
        const wsUri = `${wsProto}//${location.host}${contextPath}/websocket/${encodeURIComponent(idConversacion)}`;
        console.log('[WS] Conectando a', wsUri);

        // (Opcional) “precalienta” la sesión si tu endpoint requiere cookie
        // fetch(`${contextPath}/health`, { credentials: 'include' }).catch(()=>{});

        // Evita abrir dos veces
        if (window.wsocket && (window.wsocket.readyState === 0 || window.wsocket.readyState === 1)) {
            console.log('[WS] Ya hay un socket en curso/abierto');
            return;
        }

        let wsocket = new WebSocket(wsUri);
        window.wsocket = wsocket; // explícito

        // DOM: comprueba nulos antes de tocar estilos
        const chatContent = document.getElementById('content');
        const chatList = document.getElementById('left');
        if (chatContent && chatList && window.innerWidth <= 768) {
            if (idConversacion !== '0-0') {
                chatContent.style.display = 'block';
                chatContent.style.marginLeft = '0';
                chatList.style.display = 'none';
            } else {
                chatContent.style.display = 'none';
                chatContent.style.marginLeft = '25vw';
                chatList.style.display = 'block';
            }
        }

        // API pública
        window.send_message = function () {
            const campoTexto = document.getElementById('campoTexto');
            if (!campoTexto)
                return;
            const mensaje = `${idEmisor}@${idConversacion}_${campoTexto.value}`;
            if (wsocket.readyState === 1) {
                wsocket.send(mensaje);
            } else {
                console.warn('[WS] No está OPEN, readyState=', wsocket.readyState);
            }
        };

        window.disconnect = function () {
            try {
                if (window.wsocket && window.wsocket.readyState === WebSocket.OPEN) {
                    window.wsocket.close(1000, 'client-close');
                }
            } catch (e) {
                console.warn('No se pudo cerrar el WS:', e);
            }
        };

        // Eventos
        wsocket.addEventListener('open', evt => console.log('[WS] OPEN', evt));
        wsocket.addEventListener('close', evt => console.log('[WS] CLOSE', evt));
        wsocket.addEventListener('message', evt => {
            const campoTexto = document.getElementById('campoTexto');
            if (campoTexto)
                campoTexto.value = '';
            console.log('[WS] MSG', evt.data);
            rcActualizarTabla();
        });
        wsocket.addEventListener('error', evt => {
            console.error('[WS] ERROR', evt);
            // (Opcional) retry con backoff si lo necesitas
            // setTimeout(init, 1000);
        });
    }
})();



