/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.resourcemanager.nodesource.infrastructure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.security.KeyException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeInformation;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.ow2.proactive.authentication.crypto.Credentials;
import org.ow2.proactive.resourcemanager.authentication.Client;
import org.ow2.proactive.resourcemanager.db.RMDBManager;
import org.ow2.proactive.resourcemanager.exception.RMException;
import org.ow2.proactive.resourcemanager.nodesource.NodeSource;

import com.google.common.collect.Sets;


public class AzureInfrastructureTest {

    private AzureInfrastructure azureInfrastructure;

    private static final String rmCreds = "UlNBCjEwMjQKUlNBL0VDQi9QS0NTMVBhZGRpbmcKdaUX3K5Cx1epYuylbM3ApIbM0C1gsIZWIX6MsFhzfUZxMnB7/BeUvAFQz3lYcTEqSl2E1LWlibBbxHMCxjUMzSoOZXFKsnTxMCieWetgUcP5sCTO/Kg1UukL4xDqOgpLp1iK0FK4dYDSBBkoUn4ePBLZWu2YOb1+mPFEE2G2hxSW0DUVMXginosmRNcG5P2n1GqrDgplizEjD7G6rN6UezDGXv6MthSjP9VbFAzOSY79UTELjOhb0Rz3qfBhl4DNvae2c3ZrHJkKHL3P6GC4Zz0BvY90VKOMQj8Y8LuwdxKthWDgcmFppfSldJ8vwsEIhbwHM9bzsRCBDelMRyDYOD9km24uOMYGAmv6/EqMHRsC2w7drAhByzU/xg4OGtYaDy4xBzlHGzpq2NBCwTdx+xLiSmTFNT7U/MZ1dTTFmCUfJ25fM5ncO1rPNvLqrzdrm2x2NEhnXCTGO1aFVTUhMyLmeNi/0KmXmE51WHPyeoWxZ5/GfQT9HxUMVBei3tE8gCM6f5W4iNTZKY6Et1nVKw==";

    @Mock
    private ConnectorIaasController connectorIaasController;

    @Mock
    private NodeSource nodeSource;

    @Mock
    private Node node;

    @Mock
    private ProActiveRuntime proActiveRuntime;

    @Mock
    private NodeInformation nodeInformation;

    @Mock
    private RMDBManager dbManager;

    @Mock
    private Client client = new Client();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        azureInfrastructure = new AzureInfrastructure();
        azureInfrastructure.setRmDbManager(dbManager);
        azureInfrastructure.initializePersistedInfraVariables();
    }

    @Test
    public void testDefaultValuesOfAllParameters() {
        assertThat(azureInfrastructure.clientId, is(nullValue()));
        assertThat(azureInfrastructure.secret, is(nullValue()));
        assertThat(azureInfrastructure.domain, is(nullValue()));
        assertThat(azureInfrastructure.subscriptionId, is(nullValue()));
        assertThat(azureInfrastructure.authenticationEndpoint, is(nullValue()));
        assertThat(azureInfrastructure.managementEndpoint, is(nullValue()));
        assertThat(azureInfrastructure.resourceManagerEndpoint, is(nullValue()));
        assertThat(azureInfrastructure.graphEndpoint, is(nullValue()));
        assertThat(azureInfrastructure.rmHostname, is(not(nullValue())));
        assertThat(azureInfrastructure.connectorIaasURL, is(not(nullValue())));
        assertThat(azureInfrastructure.image, is(nullValue()));
        assertEquals("linux", azureInfrastructure.imageOSType);
        assertThat(azureInfrastructure.vmSizeType, is(nullValue()));
        assertThat(azureInfrastructure.vmUsername, is(nullValue()));
        assertThat(azureInfrastructure.vmPassword, is(nullValue()));
        assertThat(azureInfrastructure.vmPublicKey, is(nullValue()));
        assertThat(azureInfrastructure.resourceGroup, is(nullValue()));
        assertThat(azureInfrastructure.region, is(nullValue()));
        assertThat(azureInfrastructure.numberOfInstances, is(1));
        assertThat(azureInfrastructure.numberOfNodesPerInstance, is(1));
        assertThat(azureInfrastructure.nodeJarURL, is(not(nullValue())));
        assertThat(azureInfrastructure.privateNetworkCIDR, is(nullValue()));
        assertThat(azureInfrastructure.staticPublicIP, is(true));
        assertThat(azureInfrastructure.additionalProperties,
                   is("-Dproactive.useIPaddress=true -Dproactive.pnp.port=64738"));
    }

    @Test
    public void testConfigureDoNotThrowIllegalArgumentExceptionWithValidParameters() {
        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;

        try {
            azureInfrastructure.configure("clientId",
                                          "secret",
                                          "domain",
                                          "subscriptionId",
                                          "authenticationEndpoint",
                                          "managementEndpoint",
                                          "resourceManagerEndpoint",
                                          "graphEndpoint",
                                          "test.activeeon.com",
                                          "http://localhost:8088/connector-iaas",
                                          "image",
                                          "linux",
                                          "Standard_D1_v2",
                                          "vmUsername",
                                          "vmPassword",
                                          "vmPublicKey",
                                          "resourceGroup",
                                          "region",
                                          "2",
                                          "3",
                                          "test.activeeon.com/rest/node.jar",
                                          "192.168.1.0/24",
                                          true,
                                          "-Dnew=value",
                                          -1,
                                          -1,
                                          "",
                                          "",
                                          "",
                                          "",
                                          -1,
                                          "linux startup script",
                                          "windows startup script");
            Assert.assertTrue(Boolean.TRUE);
        } catch (IllegalArgumentException e) {
            fail("NPE not thrown");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void tesConfigureNotEnoughParameters() {

        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;

        azureInfrastructure.configure("clientId", "secret");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tesConfigureWithANullArgument() {

        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;

        azureInfrastructure.configure("clientId",
                                      "secret",
                                      "domain",
                                      "subscriptionId",
                                      "authenticationEndpoint",
                                      "managementEndpoint",
                                      "resourceManagerEndpoint",
                                      "graphEndpoint",
                                      "test.activeeon.com",
                                      "http://localhost:8088/connector-iaas",
                                      null,
                                      "linux",
                                      "Standard_D1_v2",
                                      "vmUsername",
                                      "vmPassword",
                                      "vmPublicKey",
                                      "resourceGroup",
                                      "region",
                                      "2",
                                      "3",
                                      "test.activeeon.com/rest/node.jar",
                                      "192.168.1.0/24",
                                      true,
                                      "-Dnew=value",
                                      -1,
                                      -1,
                                      "",
                                      "",
                                      "",
                                      "",
                                      -1,
                                      "linux startup script",
                                      "windows startup script");
    }

    @Test
    public void testAcquiringTwoNodesByRegisteringInfrastructureCreatingInstancesAndInjectingScriptOnThem()
            throws ScriptNotExecutedException, KeyException {

        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;

        azureInfrastructure.configure("clientId",
                                      "secret",
                                      "domain",
                                      "subscriptionId",
                                      "authenticationEndpoint",
                                      "managementEndpoint",
                                      "resourceManagerEndpoint",
                                      "graphEndpoint",
                                      "test.activeeon.com",
                                      "http://localhost:8088/connector-iaas",
                                      "image",
                                      "windows",
                                      "Standard_D1_v2",
                                      "vmUsername",
                                      "vmPassword",
                                      "vmPublicKey",
                                      "resourceGroup",
                                      "region",
                                      "2",
                                      "3",
                                      "http://localhost:8088/rest/node.jar",
                                      "192.168.1.0/24",
                                      true,
                                      "-Dnew=value",
                                      -1,
                                      -1,
                                      "",
                                      "",
                                      "",
                                      "",
                                      -1,
                                      "linux startup script",
                                      "windows startup script");

        azureInfrastructure.connectorIaasController = connectorIaasController;

        when(nodeSource.getAdministrator()).thenReturn(client);

        when(client.getCredentials()).thenReturn(Credentials.getCredentialsBase64(rmCreds.getBytes()));

        azureInfrastructure.setRmUrl("http://test.activeeon.com");

        when(connectorIaasController.createAzureInfrastructure("node_source_name",
                                                               "clientId",
                                                               "secret",
                                                               "domain",
                                                               "subscriptionId",
                                                               "authenticationEndpoint",
                                                               "managementEndpoint",
                                                               "resourceManagerEndpoint",
                                                               "graphEndpoint",
                                                               false)).thenReturn("node_source_name");

        when(connectorIaasController.createAzureInstances("node_source_name",
                                                          "node_source_name",
                                                          "image",
                                                          2,
                                                          "vmUsername",
                                                          "vmPassword",
                                                          "vmPublicKey",
                                                          "Standard_D1_v2",
                                                          "resourceGroup",
                                                          "region",
                                                          "192.168.1.0/24",
                                                          true)).thenReturn(Sets.newHashSet("123", "456"));

        when(connectorIaasController.createAzureInstances(anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyInt(),
                                                          anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          anyBoolean())).thenReturn(Sets.newHashSet("123",
                                                                                                    "456",
                                                                                                    "789"));

        doAnswer((Answer<Object>) invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(nodeSource).executeInParallel(any(Runnable.class));

        azureInfrastructure.acquireNode();

        verify(connectorIaasController).waitForConnectorIaasToBeUP();

        verify(connectorIaasController).createAzureInfrastructure("node_source_name",
                                                                  "clientId",
                                                                  "secret",
                                                                  "domain",
                                                                  "subscriptionId",
                                                                  "authenticationEndpoint",
                                                                  "managementEndpoint",
                                                                  "resourceManagerEndpoint",
                                                                  "graphEndpoint",
                                                                  false);

        verify(connectorIaasController).createAzureInstances("node_source_name",
                                                             "node_source_name",
                                                             "image",
                                                             1,
                                                             "vmUsername",
                                                             "vmPassword",
                                                             "vmPublicKey",
                                                             "Standard_D1_v2",
                                                             "resourceGroup",
                                                             "region",
                                                             "192.168.1.0/24",
                                                             true);

        verify(connectorIaasController, times(3)).executeScript(anyString(), anyString(), anyList());
    }

    @Test
    public void testRemoveNode() throws ProActiveException, RMException {
        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;

        azureInfrastructure.configure("clientId",
                                      "secret",
                                      "domain",
                                      "subscriptionId",
                                      "authenticationEndpoint",
                                      "managementEndpoint",
                                      "resourceManagerEndpoint",
                                      "graphEndpoint",
                                      "http://test.activeeon.com:8080",
                                      "http://localhost:8088/connector-iaas",
                                      "image",
                                      "linux",
                                      "Standard_D1_v2",
                                      "vmUsername",
                                      "vmPassword",
                                      "vmPublicKey",
                                      "resourceGroup",
                                      "region",
                                      "2",
                                      "3",
                                      "wget -nv test.activeeon.com/rest/node.jar",
                                      "192.168.1.0/24",
                                      true,
                                      "-Dnew=value",
                                      -1,
                                      -1,
                                      "",
                                      "",
                                      "",
                                      "",
                                      -1,
                                      "linux startup script",
                                      "windows startup script");

        azureInfrastructure.connectorIaasController = connectorIaasController;

        when(node.getProperty(azureInfrastructure.getInstanceIdNodeProperty())).thenReturn("123");

        when(node.getNodeInformation()).thenReturn(nodeInformation);

        when(node.getProActiveRuntime()).thenReturn(proActiveRuntime);

        when(nodeInformation.getName()).thenReturn("nodename");

        azureInfrastructure.getNodesPerInstancesMap().put("123", Sets.newHashSet("nodename"));

        azureInfrastructure.removeNode(node);

        verify(proActiveRuntime).killNode("nodename");

        verify(connectorIaasController).terminateInstance("node_source_name", "123");

        assertThat(azureInfrastructure.getNodesPerInstancesMap().isEmpty(), is(true));

    }

    @Test
    public void testThatNotifyAcquiredNodeMethodFillsTheNodesMapCorrectly() throws ProActiveException, RMException {

        when(nodeSource.getName()).thenReturn("Node source Name");
        azureInfrastructure.nodeSource = nodeSource;
        azureInfrastructure.configure("clientId",
                                      "secret",
                                      "domain",
                                      "subscriptionId",
                                      "authenticationEndpoint",
                                      "managementEndpoint",
                                      "resourceManagerEndpoint",
                                      "graphEndpoint",
                                      "http://test.activeeon.com:8080",
                                      "http://localhost:8088/connector-iaas",
                                      "image",
                                      "linux",
                                      "Standard_D1_v2",
                                      "vmUsername",
                                      "vmPassword",
                                      "vmPublicKey",
                                      "resourceGroup",
                                      "region",
                                      "2",
                                      "3",
                                      "wget -nv test.activeeon.com/rest/node.jar",
                                      "192.168.1.0/24",
                                      true,
                                      "-Dnew=value",
                                      -1,
                                      -1,
                                      "",
                                      "",
                                      "",
                                      "",
                                      -1,
                                      "linux startup script",
                                      "windows startup script");

        azureInfrastructure.connectorIaasController = connectorIaasController;

        when(node.getProperty(azureInfrastructure.getInstanceIdNodeProperty())).thenReturn("123");

        when(node.getNodeInformation()).thenReturn(nodeInformation);

        when(nodeInformation.getName()).thenReturn("nodename");

        azureInfrastructure.notifyAcquiredNode(node);

        assertThat(azureInfrastructure.getNodesPerInstancesMapCopy().get("123").isEmpty(), is(false));
        assertThat(azureInfrastructure.getNodesPerInstancesMapCopy().get("123").size(), is(1));
        assertThat(azureInfrastructure.getNodesPerInstancesMapCopy().get("123").contains("nodename"), is(true));
    }
}
